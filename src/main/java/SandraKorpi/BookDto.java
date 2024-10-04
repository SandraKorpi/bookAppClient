package SandraKorpi;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookDto {
    private int bookId;
    private String title;
    private String author;
    private int yearPublished;
    private String genre;
    private boolean recommended;

    public void setIsRecommended(boolean isRecommended) {
        this.recommended = isRecommended;
    }
}
