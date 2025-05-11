package servlet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SalesTrend {
    private int categoryId;
    private String categoryName;
    private Map<Integer, Double> monthlySales = new HashMap<>();
    private long predictedSales;
    private String confidence;

    public int getCategoryId() { return categoryId; }
    public void setCategoryId(int categoryId) { this.categoryId = categoryId; }
    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }
    public Map<Integer, Double> getMonthlySales() { return monthlySales; }
    public void setMonthlySales(Map<Integer, Double> monthlySales) { this.monthlySales = monthlySales; }
    public long getPredictedSales() { return predictedSales; }
    public void setPredictedSales(long predictedSales) { this.predictedSales = predictedSales; }
    public String getConfidence() { return confidence; }
    public void setConfidence(String confidence) { this.confidence = confidence; }

    // 格式化过去 3 个月销量为 {X月: Y}
    public String getFormattedMonthlySales() {
        if (monthlySales.isEmpty()) {
            return "{}";
        }
        StringBuilder formatted = new StringBuilder("{");
        List<Map.Entry<Integer, Double>> entries = new ArrayList<>(monthlySales.entrySet());
        entries.sort((e1, e2) -> e2.getKey().compareTo(e1.getKey())); // 按月份降序
        for (int i = 0; i < entries.size(); i++) {
            Map.Entry<Integer, Double> entry = entries.get(i);
            formatted.append(entry.getKey()).append("月: ").append(entry.getValue());
            if (i < entries.size() - 1) {
                formatted.append(", ");
            }
        }
        formatted.append("}");
        return formatted.toString();
    }
}