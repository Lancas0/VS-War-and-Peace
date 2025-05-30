package com.lancas.vswap.obsolete.ship;

/*
//每次MoveNext时运算一次而不是计算完成后
public class PointsOnPlaneIterable implements Iterable<Vector3dc>, Iterator<Vector3dc> {
    public static enum LeadAxis {
        X, Y, Z
    }

    public static final double MIN_STEP = 0.001;

    private final Planed plane;
    private final AABBd bound;
    private final double step;
    private final LeadAxis leadAxis;

    private final double axis1TravelRange;
    private final double axis2TravelRange;

    private double axis1Traveled = 0;
    private double axis2Traveled = 0;
    private int continuousAxis1TravelCnt = 0;  //0 to add +, 1 to add -
    private int continuousAxis2TravelCnt = 0;

    private Vector3dc center;
    private boolean centerGetted = false;

    public PointsOnPlaneIterable(Planed inPlane, AABBdc inBound, double inStep) {
        plane = inPlane;
        bound = new AABBd(inBound);
        step = inStep;

        double ax = Math.abs(plane.a);
        double ay = Math.abs(plane.b);
        double az = Math.abs(plane.c);

        if (ax >= ay && ax >= az) {
            leadAxis = LeadAxis.X;
            axis1TravelRange = bound.maxY - bound.minY;
            axis2TravelRange = bound.maxZ - bound.minZ;
        }
        else if (ay >= ax && ay >= az) {
            leadAxis = LeadAxis.Y;
            axis1TravelRange = bound.maxX - bound.minX;
            axis2TravelRange = bound.maxZ - bound.minZ;
        }
        else {
            leadAxis = LeadAxis.Z;
            axis1TravelRange = bound.maxX - bound.minX;
            axis2TravelRange = bound.maxY - bound.minY;
        }
        center = bound.center(new Vector3d());
    }

    @Override
    public @NotNull Iterator<Vector3dc> iterator() { return this; }

    @Override
    public boolean hasNext() {
        if (!centerGetted) return true;
        if (step < MIN_STEP) return false;

        if (axis1Traveled > axis1TravelRange && axis2Traveled > axis2TravelRange) return false;
    }

    @Override
    public Vector3dc next() {
        if (!centerGetted) {
            centerGetted = true;
            return center;
        }

        //suppose hasNext is true
        boolean travelAxis1;
        if (axis1Traveled > axis1TravelRange) {
            travelAxis1 = false;
            if (continuousAxis2TravelCnt >= 2)
                continuousAxis2TravelCnt = 0;

        } else if (axis2Traveled > axis2TravelRange) {
            travelAxis1 = true;
            if (continuousAxis1TravelCnt >= 2)
                continuousAxis1TravelCnt = 0;

        } else if (continuousAxis1TravelCnt < 2) {
            travelAxis1 = true;
        } else if (continuousAxis1TravelCnt >= 2 && continuousAxis2TravelCnt >= 2) {
            travelAxis1 = true;
            continuousAxis1TravelCnt = 0;
            continuousAxis2TravelCnt = 0;
        } else {
            travelAxis1 = false;
        }

        double x, y, z;
        switch (leadAxis) {
            case X -> {
                if (continuousAxis1TravelCnt == 0) {
                    y =
                }
                y = center.y() + axis1Traveled;
                z = center.z() + axis2Traveled;

                if (travelAxis1) {
                    if (continuousAxis1TravelCnt == 0)  //add+
                        y += step;
                    else if (continuousAxis1TravelCnt == 1) {  //add-
                        y -= step;
                        axis1Traveled += step;
                    }

                    continuousAxis1TravelCnt++;
                } else {
                    travelAxis1
                }
            }


        }


    }


    public void d() {
        if (step < MIN_STEP) return new HashSet<>();  //too much points

        HashSet<Vector3d> points = new HashSet<>();

        double ax = Math.abs(plane.a);
        double ay = Math.abs(plane.b);
        double az = Math.abs(plane.c);
        if (ax >= ay && ax >= az) {
            // 主导轴为X轴，遍历Y和Z
            for (double y = bounds.minY(); y <= bounds.maxY(); y += step) {
                for (double z = bounds.minZ(); z <= bounds.maxZ(); z += step) {
                    double xVal = (-plane.d - plane.b * y - plane.c * z) / plane.a;
                    points.add(new Vector3d(xVal, y, z));
                }
            }
        } else if (ay >= ax && ay >= az) {
            // 主导轴为Y轴，遍历X和Z
            for (double x = bounds.minX(); x <= bounds.maxX(); x += step) {
                for (double z = bounds.minZ(); z <= bounds.maxZ(); z++) {
                    double yVal = (-plane.d - plane.a * x - plane.c * z) / plane.b;
                    points.add(new Vector3d(x, yVal, z));
                }
            }
        } else {
            // 主导轴为Z轴，遍历X和Y
            for (double x = bounds.minX(); x <= bounds.maxX(); x += step) {
                for (double y = bounds.minY(); y <= bounds.maxY(); y += step) {
                    double zVal = (-plane.d - plane.a * x - plane.b * y) / plane.c;
                    points.add(new Vector3d(x, y, zVal));
                }
            }
        }

        return points;
    }
    }
}
*/