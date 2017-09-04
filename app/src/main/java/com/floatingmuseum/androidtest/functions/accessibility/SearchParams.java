package com.floatingmuseum.androidtest.functions.accessibility;

import android.util.SparseArray;
import android.util.SparseBooleanArray;
import android.view.accessibility.AccessibilityNodeInfo;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Floatingmuseum on 2017/9/4.
 */

public class SearchParams {
    private Class targetViewClass;
    private String targetText;
    private String[] preconditions;
    private Map<String, Boolean> preconditionsResults;
    private String[] preconditionsWithSameParent;
    private Map<String, AccessibilityNodeInfo> preconditionsWithSameParentResults;

    public SearchParams( Class targetViewClass, String targetText, String[] preconditions, String[] preconditionsWithSameParent) {
        this.targetViewClass = targetViewClass;
        this.targetText = targetText;
        this.preconditions = preconditions;
        if (preconditions != null) {
            preconditionsResults = new HashMap<>();
            for (String precondition : preconditions) {
                preconditionsResults.put(precondition, false);
            }
        }
        this.preconditionsWithSameParent = preconditionsWithSameParent;
        if (preconditionsWithSameParent != null) {
            preconditionsWithSameParentResults = new HashMap<>();
            for (String precondition : preconditionsWithSameParent) {
                preconditionsWithSameParentResults.put(precondition, null);
            }
        }
    }

    public Class getTargetViewClass() {
        return targetViewClass;
    }

    public void setTargetViewClass(Class targetViewClass) {
        this.targetViewClass = targetViewClass;
    }

    public String getTargetText() {
        return targetText;
    }

    public void setTargetText(String targetText) {
        this.targetText = targetText;
    }

    public boolean confirmPrecondition(String precondition) {
        if (preconditionsResults != null && preconditionsResults.containsKey(precondition)) {
            if (!preconditionsResults.get(precondition)) {
                preconditionsResults.put(precondition, true);
            }
            return true;
        } else {
            return false;
        }
    }

    public boolean isPreconditionsSatisfied() {
        boolean satisfied = true;
        if (preconditionsResults != null) {
            for (String precondition : preconditionsResults.keySet()) {
                if (!preconditionsResults.get(precondition)) {
                    satisfied = false;
                }
            }
        }
        return satisfied;
    }

    public boolean confirmPreconditionsWithSameParent(String precondition, AccessibilityNodeInfo node) {
        if (preconditionsWithSameParentResults != null && preconditionsWithSameParentResults.containsKey(precondition)) {
            if (preconditionsWithSameParentResults.size() == 0) {
                preconditionsWithSameParentResults.put(precondition, node);
                return true;
            } else {
                boolean isSameParent = false;
                for (String key : preconditionsWithSameParentResults.keySet()) {
                    if (preconditionsWithSameParentResults.get(key).getParent().equals(node.getParent())) {
                        isSameParent = true;
                        break;
                    }
                }
                if (isSameParent) {
                    preconditionsWithSameParentResults.put(precondition, node);
                    return true;
                }
                return false;
            }
        } else {
            return false;
        }
    }

    public boolean isPreconditionsWithSameParentSatisfied() {
        boolean satisfied = true;
        if (preconditionsWithSameParentResults != null) {
            for (String precondition : preconditionsWithSameParentResults.keySet()) {
                if (preconditionsWithSameParentResults.get(precondition)==null) {
                    satisfied = false;
                }
            }
        }
        return satisfied;
    }
}
