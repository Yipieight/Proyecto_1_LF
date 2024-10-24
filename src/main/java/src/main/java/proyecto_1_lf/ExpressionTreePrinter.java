package src.main.java.proyecto_1_lf;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

class ExpressionTreePrinter {

    public static void printTreeToFile(TreeNode node, String filePath) throws IOException {
        BufferedWriter escritor = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filePath), "UTF-8"));
        try {
            printTree(node, "", true, escritor);
        } finally {
            escritor.close();  
        }
    }

    public static void printTree(TreeNode node, String prefix, boolean isLeft, BufferedWriter escritor) throws IOException {
        if (node == null) {
            return;
        }

        escritor.write(prefix + (isLeft ? "├── " : "└── ") + node.toString());
        escritor.newLine();
        if (node instanceof OperatorNode) {
            OperatorNode opNode = (OperatorNode) node;
            printTree(opNode.getLeft(), prefix + (isLeft ? "│   " : "    "), true, escritor);
            printTree(opNode.getRight(), prefix + (isLeft ? "│   " : "    "), false, escritor);
        } else if (node instanceof UnaryOperatorNode) {
            UnaryOperatorNode unaryNode = (UnaryOperatorNode) node;
            printTree(unaryNode.getChild(), prefix + (isLeft ? "│   " : "    "), false, escritor);
        }
    }
}
