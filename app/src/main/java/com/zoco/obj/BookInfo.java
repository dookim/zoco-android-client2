package com.zoco.obj;

/**
 * Created by dookim on 2/8/15.
 */
public class BookInfo {
    public String email;
    public String isbn;
    public String author;
    public int ori_price;
    public int price;
    public int scribble;
    public int check_answer;
    public int have_answer;
    public String img_str;
    public String title;

    //추가적인 문제점은 업을까 ?
    //backend에는 데이터는 저렇게
    //변환과정이 필요하다는 생각이 듬.

    public BookInfo(String email, String isbn, String author, int ori_price, int price, boolean scribble, boolean have_answer, boolean check_answer, String img_str,String title) {
        this.email = email;
        this.isbn = isbn;
        this.author = author;
        this.ori_price = ori_price;
        this.price = price;
        this.scribble = scribble?1:0;
        this.check_answer = check_answer?1:0;
        this.have_answer = have_answer?1:0;
        this.img_str = img_str;
        this.title = title;
    }
}
