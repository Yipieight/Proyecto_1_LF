package src.main.java.proyecto_1_lf;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

abstract class TreeNode {
    public abstract Set<Integer> first();
    public abstract Set<Integer> last();
    public abstract boolean nullable();
    public abstract void calculateFollow(Map<Integer, Set<Integer>> followMap);
    public abstract String toString();
}

class OperandNode extends TreeNode {
    private String value;  
    private int id;  

    public OperandNode(String value, int id) {
        this.value = value;
        this.id = id;
    }

    public String getValue() {
        return value;
    }

    public int getId() {
        return id;
    }

    @Override
    public Set<Integer> first() {
        Set<Integer> result = new HashSet<>();
        result.add(id);
        return result;
    }

    @Override
    public Set<Integer> last() {
        Set<Integer> result = new HashSet<>();
        result.add(id);
        return result;
    }

    @Override
    public boolean nullable() {
        return false;  
    }

    @Override
    public void calculateFollow(Map<Integer, Set<Integer>> followMap) {
    }

    @Override
    public String toString() {
        return value + "[" + id + "]";
    }
}


class OperatorNode extends TreeNode {
    private String operator;  
    private TreeNode left, right;

    public OperatorNode(String operator, TreeNode left, TreeNode right) {
        this.operator = operator; 
        this.left = left;
        this.right = right;
    }

    public TreeNode getLeft() {
        return left;
    }

    public TreeNode getRight() {
        return right;
    }

    @Override
    public Set<Integer> first() {
        Set<Integer> result = new HashSet<>();
        if (operator.equals("|")) {
            result.addAll(left.first());
            result.addAll(right.first());
        } else if (operator.equals(".")) {
            if (left.nullable()) {
                result.addAll(left.first());
                result.addAll(right.first());  
            } else {
                result.addAll(left.first());  
            }
        }
        return result;
    }

    @Override
    public Set<Integer> last() {
        Set<Integer> result = new HashSet<>();
        if (operator.equals("|")) {
            result.addAll(left.last());
            result.addAll(right.last());
        } else if (operator.equals(".")) {
            if (right.nullable()) {
                result.addAll(left.last());
                result.addAll(right.last());
            } else {
                result.addAll(right.last());
            }
        }
        return result;
    }

    @Override
    public boolean nullable() {
        if (operator.equals("|")) {
            return left.nullable() || right.nullable();
        } else if (operator.equals(".")) {
            return left.nullable() && right.nullable();
        }
        return false;
    }

    @Override
    public void calculateFollow(Map<Integer, Set<Integer>> followMap) {
        if (operator.equals(".")) {
            for (int lastId : left.last()) {
                followMap.computeIfAbsent(lastId, k -> new HashSet<>()).addAll(right.first());
            }
        }
        left.calculateFollow(followMap);
        right.calculateFollow(followMap);
    }

    @Override
    public String toString() {
        return operator;
    }
}

class UnaryOperatorNode extends TreeNode {
    private String operator;  
    private TreeNode child;

    public UnaryOperatorNode(String operator, TreeNode child) {
        this.operator = operator;  
        this.child = child;
    }

    @Override
    public Set<Integer> first() {
        return child.first();
    }

    @Override
    public Set<Integer> last() {
        return child.last();
    }

    @Override
    public boolean nullable() {
        if (operator.equals("?")) {
            return true;  
        } else if (operator.equals("+")) {
            return child.nullable();  
        } else if (operator.equals("*")) {
            return true;  
        }
        return false;
    }

    @Override
    public void calculateFollow(Map<Integer, Set<Integer>> followMap) {
        if (operator.equals("*") || operator.equals("+")) {
            for (int lastId : child.last()) {
                followMap.computeIfAbsent(lastId, k -> new HashSet<>()).addAll(child.first());
            }
        }
        child.calculateFollow(followMap);
    }

    public TreeNode getChild() {
        return child;
    }

    @Override
    public String toString() {
        return operator;
    }
}