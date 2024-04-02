package com.example.sal.Model;

public class ItemDictionary {
    private String termin;
    private String ponyatie;
    private String id;

    public ItemDictionary() {
        // Пустой конструктор без аргументов
    }

    public ItemDictionary(String termin, String ponyatie) {
        this.termin = termin;
        this.ponyatie = ponyatie;
    }

    public String getTermin() {
        return termin;
    }

    public void setTermin(String termin) {
        this.termin = termin;
    }

    public String getPonyatie() {
        return ponyatie;
    }

    public void setPonyatie(String ponyatie) {
        this.ponyatie = ponyatie;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}