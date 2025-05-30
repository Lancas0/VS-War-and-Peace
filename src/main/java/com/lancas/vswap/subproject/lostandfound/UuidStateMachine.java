package com.lancas.vswap.subproject.lostandfound;

import com.lancas.vswap.debug.EzDebug;

import java.util.HashMap;
import java.util.function.Consumer;

//have a generic T which is LostAndFound
//so the UuidStateMachine act like friend class of LostAndFound
public class UuidStateMachine<T extends LostAndFound> {
    protected T lostAndFound;

    public UuidStateMachine(T inLAF) {
        lostAndFound = inLAF;

        //I do this because I don't want to miss any state value
        for (UuidState.State state : UuidState.State.values()) {
            int a = switch (state) {
                case Alive -> {
                    onDestroyHandler.put(state, UuidState::setMissing);  // on alive destroyed : set as missing
                    yield 0;
                }
                case Missing -> {
                    onDestroyHandler.put(state, u -> {
                        u.setMissing();  //reset the count down
                        EzDebug.warn("BE hold a uuid with a Missing state should be already remove! But there is a to-replace be hold this uuid");
                    });
                    yield 1;
                }
                case WaitAndSee -> {
                    onDestroyHandler.put(state, u -> {
                        //this means there use to be a suspected claim, when the old one have not been removed
                        //now it turns out the claim should really be done
                        lostAndFound.reclaimWaitAndSee(u);
                    });
                    yield 2;
                }
                case Dead -> {
                    onDestroyHandler.put(state, u -> {
                        u.setMissing();  //reset the count down anyway
                        EzDebug.warn("BE hold a uuid with a Dead state should be already remove! But there is a to-replace be hold this uuid");
                    });
                    yield 3;
                }
                case Forget -> {
                    onDestroyHandler.put(state, u -> {
                        u.setMissing();  //reset the count down anyway
                        EzDebug.warn("BE hold a uuid with a Forget state should be already remove! But there is a to-replace be hold this uuid");
                    });
                    yield 4;
                }
            };
        }
    }

    public HashMap<UuidState.State, Consumer<UuidState>> onDestroyHandler = new HashMap<>();


    /*public static HashMap<UuidState.State, TriConsumer<LostAndFound, UuidState, BlockPos>> blockPreReplaceHandler = new HashMap<>();
    public static HashMap<UuidState.State, BiConsumer<LostAndFound, UuidState>> lazyTickHandler = new HashMap<>();

    static {

        //I do this because I don't want to miss any state value
        for (UuidState.State state : UuidState.State.values()) {
            int a = switch (state) {
                case Alive -> {
                    blockPreReplaceHandler.put(state, (laf, u, bp) -> u.state = UuidState.State.Missing);
                    lazyTickHandler.put(state, (laf, u) -> {});
                    yield 0;
                }
                case Missing -> {
                    blockPreReplaceHandler.put(UuidState.State.Missing, (laf, u, bp) -> {
                        u.state = UuidState.State.Missing;
                        EzDebug.warn("BE hold a uuid with a Missing state should be already remove! But there is a to-replace be hold this uuid");
                    });
                }
                case WaitAndSee -> {

                }
                case Dead -> {
                    blockPreReplaceHandler.put(state, (laf, u, bp) -> {
                        u.state = UuidState.State.Missing;
                        EzDebug.warn("A uuid with a Dead state should not be hold by a be! But there is a to-replace be hold this uuid");
                    });

                }
                case Forget -> {
                    blockPreReplaceHandler.put(UuidState.State.Forget, (laf, u, bp) -> {
                        u.state = UuidState.State.Forget;
                        EzDebug.error("BE hold a uuid with a Forget state should be already remove! But there is a to-replace be hold this uuid");
                    });
                    blockPreReplaceHandler.put(UuidState.State.WaitAndSee, (laf, u, bp) -> {
                        u.state = UuidState.State.Alive;  //there is a wait and see which involve two be finally handled.
                        //the block be removed means the other block should be set as
                        BlockPos invBp1 = u.involveBp1;
                        BlockPos invBp2 = u.involveBp2;

                        if (invBp1 == null || invBp2 == null) {
                            EzDebug.error("the uuidState with WaitAndSee must have two inv bps.");
                            //todo handle this exceptional state
                            return;
                        }



                        if (invBp1.equals(bp)) {

                        }
                    });
                }
            };
        }
    }*/
}
