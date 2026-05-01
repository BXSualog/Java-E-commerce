package functions;

public class Review {
    private int reviewId;
    private int productId;
    private int customerId;
    private int rating; // 1-5
    private String comment;

    public Review(int reviewId, int productId, int customerId, int rating, String comment) {
        this.reviewId = reviewId;
        this.productId = productId;
        this.customerId = customerId;
        this.rating = rating;
        this.comment = comment;
    }

    public Review() {
        this.reviewId = 0;
        this.productId = 0;
        this.customerId = 0;
        this.rating = 0;
        this.comment = "";
    }

    public int getReviewId() { return reviewId; }
    public int getProductId() { return productId; }
    public int getCustomerId() { return customerId; }
    public int getRating() { return rating; }
    public String getComment() { return comment; }

    // serialization format: reviewId,productId,customerId,rating,comment
    public String toCSV() {
        return reviewId + "," + productId + "," + customerId + "," + rating + "," + comment;
    }

    public static Review fromCSV(String csv) {
        String[] parts = csv.split(",");
        if (parts.length == 5) {
            return new Review(
                Integer.parseInt(parts[0]),
                Integer.parseInt(parts[1]),
                Integer.parseInt(parts[2]),
                Integer.parseInt(parts[3]),
                parts[4]
            );
        }
        return null;
    }
}
