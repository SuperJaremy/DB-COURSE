package edu.course.dbcourse.beans;

import edu.course.dbcourse.db.DataBaseBean;
import edu.course.dbcourse.objects.Article;
import lombok.Getter;
import lombok.Setter;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.bean.ViewScoped;
import java.util.ArrayList;
import java.util.List;

@ManagedBean(name = "articles")
@SessionScoped
public class ArticlesBean {

    @ManagedProperty("#{database}")
    private @Setter
    DataBaseBean db;

    private List<Article> allArticles;
    private @Getter List<Article> currentFrame;

    private @Getter
    int currentPage = 1;

    private final int ARTICLES_ON_PAGE = 10;

    private @Getter
    boolean canMoveForward = true;

    private @Getter
    boolean canMoveBackward = false;

    @PostConstruct
    public void init() {
        allArticles = new ArrayList<>();
        currentFrame = new ArrayList<>(ARTICLES_ON_PAGE);
        Article article = null;
        for (int i = 0; i < ARTICLES_ON_PAGE; i++) {
            article = Article.getNextArticle(db, article);
            if (article != null) {
                allArticles.add(article);
                currentFrame.add(article);
            } else {
                canMoveForward = false;
                return;
            }
        }
    }

    public void moveForward() {
        if (!canMoveForward)
            return;
        if (allArticles.size() <= currentPage * ARTICLES_ON_PAGE) {
            Article article = allArticles.get(allArticles.size() - 1);
            List<Article> newCurrentFrame = new ArrayList<>();
            int i = 0;
            for (; i < ARTICLES_ON_PAGE; i++) {
                article = Article.getNextArticle(db, article);
                if (article != null) {
                    allArticles.add(article);
                    newCurrentFrame.add(article);
                } else {
                    canMoveForward = false;
                    break;
                }
            }
            if (i > 0) {
                currentFrame = newCurrentFrame;
                currentPage++;
                canMoveBackward = true;
            }
        }
        else{
            currentFrame = new ArrayList<>();
            int border = Math.min(ARTICLES_ON_PAGE, allArticles.size() - currentPage * ARTICLES_ON_PAGE);
            int base = currentPage*ARTICLES_ON_PAGE;
            for (int i = 0; i < border; i++) {
                currentFrame.add(allArticles.get(i + base));
            }
            canMoveForward = border == ARTICLES_ON_PAGE;
            canMoveBackward = currentPage > 1;
            currentPage++;
        }
        if(currentPage*ARTICLES_ON_PAGE >= allArticles.size() &&
                Article.getNextArticle(db, allArticles.get(allArticles.size() - 1)) == null)
            canMoveForward = false;
    }

    public void moveBackward() {
        if (!canMoveBackward || currentPage <= 1)
            return;
        currentPage--;
        canMoveForward = true;
        currentFrame = new ArrayList<>();
        for (int i = 0; i < ARTICLES_ON_PAGE; i++) {
            currentFrame.add(allArticles.get((currentPage - 1) * ARTICLES_ON_PAGE + i));
        }
        canMoveBackward = currentPage > 1;
    }
}
