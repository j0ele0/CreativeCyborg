package com.example.creativcyborg.entities;

/**
 * Defininiert den Aufbau eines Inhaltsteils.
 */
public class ContentPart
{
    /**
     * Die von ChatGPT-generierte Idee,
     */
    private String idea;

    /**
     * Ein optionaler Link zum Bild.
     */
    private String imageLink;

    public ContentPart()
    {
    }

    public ContentPart(String idea, String imageLink)
    {
        this.idea = idea;
        this.imageLink = imageLink;
    }

    public void setIdea(String idea) {
        this.idea = idea;
    }

    public void setImageLink(String imageLink) {
        this.imageLink = imageLink;
    }

    public String getIdea() {
        return idea;
    }

    public String getImageLink() {
        return imageLink;
    }
}
