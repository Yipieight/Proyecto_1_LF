package src.main.java.proyecto_1_lf;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

public class ExpressionTreeExcelExporter {

    public static void exportToExcel(TreeNode root) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("ExpressionTreeData");

        String[] headers = {"SÍMBOLO", "FIRST", "LAST", "NULLABLE"};
        Row headerRow = sheet.createRow(0);
        CellStyle headerStyle = workbook.createCellStyle();
        headerStyle.setFillForegroundColor(IndexedColors.LIGHT_BLUE.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        Font font = workbook.createFont();
        font.setColor(IndexedColors.WHITE.getIndex());
        headerStyle.setFont(font);

        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);  
        }

        List<TreeNode> nodes = new ArrayList<>();
        collectNodes(root, nodes);

        int rowNum = 1;
        for (TreeNode node : nodes) {
            Row row = sheet.createRow(rowNum++);
            String symbol = obtenerSimbolo(node.toString());
            String first = formatSet(node.first());
            String last = formatSet(node.last());
            boolean nullable = node.nullable();

            row.createCell(0).setCellValue(symbol);
            row.createCell(1).setCellValue(first);
            row.createCell(2).setCellValue(last);
            row.createCell(3).setCellValue(nullable ? "True" : "False");
        }

        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }

        FileOutputStream fileOut = new FileOutputStream("expression_tree_data.xlsx");
        workbook.write(fileOut);
        fileOut.close();
        workbook.close();
    }

    private static String formatSet(Set<Integer> set) {
        StringBuilder sb = new StringBuilder();
        for (Integer num : set) {
            sb.append(num).append(", ");
        }
        return sb.length() > 0 ? sb.substring(0, sb.length() - 2) : "";
    }

    private static void collectNodes(TreeNode node, List<TreeNode> nodes) {
        if (node == null) return;

        nodes.add(node);
        if (node instanceof OperatorNode) {
            OperatorNode opNode = (OperatorNode) node;
            collectNodes(opNode.getLeft(), nodes);
            collectNodes(opNode.getRight(), nodes);
        } else if (node instanceof UnaryOperatorNode) {
            UnaryOperatorNode unaryNode = (UnaryOperatorNode) node;
            collectNodes(unaryNode.getChild(), nodes);
        }
    }

    private static String obtenerSimbolo(String simboloConID) {
        int index = simboloConID.indexOf('[');
        if (index != -1) {
            return simboloConID.substring(0, index);  
        }
        return simboloConID;  
    }

    public static void exportFollowsToExcel(Map<Integer, Set<Integer>> followMap) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet followSheet = workbook.createSheet("Follows");
    
        Row header = followSheet.createRow(0);
        Cell headerCell1 = header.createCell(0);
        Cell headerCell2 = header.createCell(1);
        
        headerCell1.setCellValue("SÍMBOLO");
        headerCell2.setCellValue("FOLLOW");
    
        CellStyle headerStyle = workbook.createCellStyle();
        headerStyle.setFillForegroundColor(IndexedColors.LIGHT_BLUE.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
    
        Font font = workbook.createFont();
        font.setColor(IndexedColors.WHITE.getIndex());
        headerStyle.setFont(font);
    
        headerCell1.setCellStyle(headerStyle);
        headerCell2.setCellStyle(headerStyle);
    
        int rowNum = 1;
        for (Map.Entry<Integer, Set<Integer>> entry : followMap.entrySet()) {
            Row row = followSheet.createRow(rowNum++);
            row.createCell(0).setCellValue(entry.getKey());
            row.createCell(1).setCellValue(entry.getValue().toString());  
        }
    
        try (FileOutputStream fileOut = new FileOutputStream("arbol_expression_follows.xlsx")) {
            workbook.write(fileOut);
        }
        workbook.close();
    }
    
}
