package com.lancas.vswap.foundation.handler.multiblock.alog;

import org.jetbrains.annotations.Nullable;
import org.joml.primitives.Rectanglei;

import java.util.ArrayDeque;
import java.util.Deque;

public class HorizonMaxRectSolver {
    public @Nullable Rectanglei getMaxRect(boolean[][] matrix) {
        if (matrix == null || matrix.length == 0 || matrix[0].length == 0) {
            //return new int[]{0, 0, 0, 0};
            //return new Rectanglei(0, 0, 0, 0);
            return null;
        }

        int cols = matrix[0].length;
        int[] heights = new int[cols];
        //int maxArea = 0;
        //int startX = 0, startY = 0, maxWidth = 0, maxHeight = 0;

        HistogramResult maxAreaResult = new HistogramResult(0, 0, 0, 0);
        Rectanglei rect = new Rectanglei(
            Integer.MAX_VALUE, Integer.MAX_VALUE,
            Integer.MIN_VALUE, Integer.MIN_VALUE
        );

        for (int i = 0; i < matrix.length; i++) {
            // 更新高度数组
            for (int j = 0; j < cols; j++) {
                heights[j] = matrix[i][j] ? heights[j] + 1 : 0;
            }

            // 调用方法计算当前行的最大矩形
            HistogramResult result = getHistogramResult(heights);
            if (result.area == 0) continue;

            int currentStartY = i - result.height + 1;

            if (result.area > maxAreaResult.area) {
                maxAreaResult = result;

                rect.setMin(result.startX, currentStartY);
                rect.setMax(result.startX + result.width - 1, currentStartY + result.height - 1);
            } /*else if (result.area == maxArea) {
                // 面积相同时选择更左上的坐标
                if (currentStartY < startY ||
                    (currentStartY == startY && result.startX < startX)) {
                    shouldUpdate = true;
                }
            }*/

            /*if (shouldUpdate) {
                maxArea = result.area;
                startX = result.startX;
                startY = currentStartY;
                maxWidth = result.width;
                maxHeight = result.height;
            }*/
        }

        return maxAreaResult.area == 0 ?
            null :
            rect;
    }

    // 辅助类，用于存储柱状图计算结果
    private static class HistogramResult {
        int startX;
        int width;
        int height;
        int area;

        HistogramResult(int startX, int width, int height, int area) {
            this.startX = startX;
            this.width = width;
            this.height = height;
            this.area = area;
        }
    }

    // 计算柱状图中的最大矩形
    private HistogramResult getHistogramResult(int[] heights) {
        int cols = heights.length;
        int[] left = new int[cols];
        int[] right = new int[cols];
        Deque<Integer> stack = new ArrayDeque<>();

        // 计算左边界
        for (int j = 0; j < cols; j++) {
            while (!stack.isEmpty() && heights[stack.peek()] >= heights[j]) {
                stack.pop();
            }
            left[j] = stack.isEmpty() ? -1 : stack.peek();
            stack.push(j);
        }

        stack.clear();

        // 计算右边界
        for (int j = cols - 1; j >= 0; j--) {
            while (!stack.isEmpty() && heights[stack.peek()] >= heights[j]) {
                stack.pop();
            }
            right[j] = stack.isEmpty() ? cols : stack.peek();
            stack.push(j);
        }

        // 寻找最大矩形
        int maxArea = 0;
        int startX = 0, width = 0, height = 0;

        for (int j = 0; j < cols; j++) {
            int currentWidth = right[j] - left[j] - 1;
            int currentHeight = heights[j];
            int currentArea = currentWidth * currentHeight;

            if (currentArea > maxArea) {
                maxArea = currentArea;
                startX = left[j] + 1;
                width = currentWidth;
                height = currentHeight;
            } /*else if (currentArea == maxArea) {
                // 选择更左的坐标
                int currentStartX = left[j] + 1;
                if (currentStartX < startX) {
                    startX = currentStartX;
                    width = currentWidth;
                    height = currentHeight;
                }
            }*/
        }

        return new HistogramResult(startX, width, height, maxArea);
    }

}



