package pl.com.bottega.jpatraining.reflection;


import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class TitleChangerTest {

    private Auction auction = new Auction("test");
    private Book book = new Book();
    private TitleChanger sut = new TitleChanger();

    @Test
    public void changesTitleOnAnyObject() {
        // when
        sut.changeTitle(auction, "new title");
        sut.changeTitle(book, "new title");

        // then
        assertThat(book.title).isEqualTo("new title");
        assertThat(auction.getTitle()).isEqualTo("new title");
    }

    @Test
    public void getsTitleOfAnyObject() {
        // given
        book.title = "test";

        // when
        String bookTitle = sut.getTitle(book);
        String auctionTitle = sut.getTitle(auction);

        // then
        assertThat(bookTitle).isEqualTo("test");
        assertThat(auctionTitle).isEqualTo("test");
    }

}
