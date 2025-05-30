package com.lancas.vswap.foundation.api.math;

import java.util.Objects;

/*public interface IPidController<T> {
    public static final double MAX_INTEGRAL = 100;

    public double getP();
    public double getI();
    public double getD();

    //private T lastErr;
    //private T integral;


    public T getError(T current, T target);
    public T getDerivative(T curErr, T lastErr, double dt);
    public T getIntegral(T lastIntegral, T error, double dt);

}*/
public record Pid(double p, double i, double d) {
    @Override
    public String toString() {
        return "Pid{" +
            "p=" + p +
            ", i=" + i +
            ", d=" + d +
            '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Pid pid = (Pid) o;
        return Double.compare(p, pid.p) == 0 && Double.compare(i, pid.i) == 0 && Double.compare(d, pid.d) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(p, i, d);
    }
}
