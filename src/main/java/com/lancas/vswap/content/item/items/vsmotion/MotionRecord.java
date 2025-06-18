package com.lancas.vswap.content.item.items.vsmotion;

import com.lancas.vswap.debug.EzDebug;
import com.lancas.vswap.subproject.sandbox.api.data.ITransformPrimitive;
import com.lancas.vswap.subproject.sandbox.api.data.TransformPrimitive;
import com.lancas.vswap.subproject.sandbox.component.data.writer.IRigidbodyDataWriter;
import com.lancas.vswap.util.NbtBuilder;
import com.simibubi.create.foundation.utility.FilesHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.nbt.NbtIo;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraftforge.fml.loading.FMLPaths;
import org.joml.Vector3d;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.FileAttribute;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.zip.GZIPInputStream;

public class MotionRecord implements IWriteOnlySavableMotion, IPlayableMotion {
    protected final List<TransformPrimitive> frames = new ArrayList<>();
    protected final Vector3d scale = new Vector3d(1, 1, 1);

    public MotionRecord() { }

    public MotionRecord withScale(double s) { scale.set(s, s, s); return this; }

    protected double prevFrame = 0;
    @Override
    public void setCurrentFrame(double frame) {
        prevFrame = frame;
    }
    @Override
    public boolean play(IRigidbodyDataWriter rigidWriter, double speed) {  //FIXME current don't support reverse
        double targetFrame = prevFrame + speed;
        //int frameCount = frames.size();

        // 边界检查：目标帧超出范围
        if (prevFrame >= frames.size()) return false;

        speed = Math.min(frames.size() - prevFrame, speed);
        while (prevFrame + speed >= Math.ceil(prevFrame) + 1E-4) {
            double played = playToNextIntFrame(rigidWriter);
            if (played < 1E-4)  //play too much, consider as finish
                return false;

            speed -= played;
        }

        //now prevFrame + speed < Math.ceil(prec), or to say we just use prevFrame to play is ok
        if (speed > 1E-4) {
            TransformPrimitive delta = new TransformPrimitive().lerp(frames.get((int)Math.floor(prevFrame)), speed).setScale(0);
            rigidWriter.update(r -> r.transform.addDelta(delta));
            prevFrame += speed;
        }
        return prevFrame <= frames.size();
    }
    public double playToNextIntFrame(IRigidbodyDataWriter rigidWriter) {
        int useFrame = (int)Math.floor(prevFrame);
        if (useFrame >= frames.size())
            return 0;

        double deltaProgress = Math.floor(prevFrame) + 1 - prevFrame;

        TransformPrimitive delta = new TransformPrimitive().lerp(frames.get(useFrame), deltaProgress).setScale(0);
        rigidWriter.update(r -> r.transform.addDelta(delta));

        prevFrame = Math.floor(prevFrame) + 1;
        return deltaProgress;
    }
    /*public static void playAtFrameFor(MotionRecord record, IRigidbodyDataWriter rigidWriter, int atFrame, double frameCnt) {
        if (atFrame >= record.frames.size())
            return;

        TransformPrimitive delta = new TransformPrimitive().lerp(frames.get(atFrame), frameCnt).setScale(0);
        rigidWriter.update(r -> r.transform.addDelta(delta));

        prevFrame = Math.ceil(prevFrame);
    }*/

    public boolean isEmpty() { return frames.isEmpty(); }

    //todo do i have to record it?
    protected TransformPrimitive lastRecordTransform = new TransformPrimitive();
    @Override
    public void addFrame(ITransformPrimitive transform) {
        if (frames.isEmpty()) {
            lastRecordTransform = transform.copy();
            frames.add(new TransformPrimitive());
        } else {
            frames.add(lastRecordTransform.deltaFromTo(transform, new TransformPrimitive()));
            lastRecordTransform = transform.copy();
        }
    }



    /*@Override
    public CompoundTag serializeNBT() {
        return new NbtBuilder()
            .putStream("motion",
                frames.stream()
                    .flatMap(x -> Stream.of(
                        NbtBuilder.tagOfVector3f(x.position.get(new Vector3f())),
                        NbtBuilder.tagOfQuaternionf(x.rotation.get(new Quaternionf()))
                    ))
            ).get();
    }

    @Override
    public void deserializeNBT(CompoundTag tag) {
        NbtBuilder.modify(tag)
            .readEachCompoundDo()
    }*/
    public CompoundTag serializeNBT() {
        return new NbtBuilder()
            .putEach("frames", frames, TransformPrimitive::saved)
            .get();
    }

    @Override
    public void deserializeNBT(CompoundTag tag) {
        frames.clear();
        NbtBuilder.modify(tag)
            .readEachCompoundDo(
                "frames",
                t -> frames.add(new TransformPrimitive().load(t))
            );
    }

    public static final Path MOTION_PATH = FMLPaths.GAMEDIR.get().resolve("motion");
    public static final String FALLBACK_FILENAME = "motion";

    public static boolean saveRecord(String fileName, boolean overwrite, IWriteOnlySavableMotion record) {
        if (record.isEmpty()) {
            EzDebug.warn("motions is empty!");
            return false;
        }

        CompoundTag nbt = record.serializeNBT();

        if (fileName == null || fileName.isEmpty()) {
            fileName = FALLBACK_FILENAME;
        }
        if (!overwrite) {
            fileName = FilesHelper.findFirstValidFilename(fileName, MOTION_PATH, "nbt");
        }
        if (!fileName.endsWith(".nbt")) {
            fileName = fileName + ".nbt";
        }

        Path file = MOTION_PATH.resolve(fileName).toAbsolutePath();
        try {
            Files.createDirectories(MOTION_PATH, new FileAttribute[0]);
            boolean overwritten = Files.deleteIfExists(file);
            OutputStream out = Files.newOutputStream(file, StandardOpenOption.CREATE);
            NbtIo.writeCompressed(nbt, out);
            if (out != null) {
                out.close();
            }
            return true;
        } catch (IOException e) {
            EzDebug.error("An error occurred while saving schematic [" + fileName + "]" + e.toString());
            e.printStackTrace();
            return false;
        }
    }


    public static MotionRecord loadMotion(String modId, String name) {
        return loadMotion(Minecraft.getInstance().getResourceManager(), new ResourceLocation(modId, name));
    }
    public static MotionRecord loadMotion(ResourceLocation location) {
        return loadMotion(Minecraft.getInstance().getResourceManager(), location);
    }
    public static MotionRecord loadMotion(ResourceManager resourceManager, ResourceLocation location) {
        String namespace = location.getNamespace();
        String path = "motion/" + location.getPath() + ".nbt";
        ResourceLocation location1 = new ResourceLocation(namespace, path);
        Optional<Resource> optionalResource = resourceManager.getResource(location1);
        if (optionalResource.isPresent()) {
            Resource resource = optionalResource.get();
            try {
                InputStream inputStream = resource.open();
                MotionRecord record = loadMotionRecord(inputStream);
                inputStream.close();
                return record;
            } catch (IOException e) {
                EzDebug.error("Failed to read ponder schematic: " + location1);
                e.printStackTrace();
            }
        } else {
            EzDebug.warn("Ponder schematic missing: " + location1);
        }
        return new MotionRecord();
    }
    protected static MotionRecord loadMotionRecord(InputStream resourceStream) throws IOException {
        MotionRecord record = new MotionRecord();
        //StructureTemplate t = new StructureTemplate();
        DataInputStream stream = new DataInputStream(new BufferedInputStream(new GZIPInputStream(resourceStream)));
        CompoundTag nbt = NbtIo.read(stream, new NbtAccounter(536870912L));
        //NbtBuilder.modify(nbt).readEachCompoundDo("motion", t -> record.frames.add(new TransformPrimitive().load(t)));
        record.deserializeNBT(nbt);
        return record;
    }
}
