package br.com.leinadlarama.diadobatecabeca.model;

import android.support.annotation.NonNull;

/**
 * Created by eumagnun on 21/01/2017.
 */

public class Event implements  Comparable<Event>{
    private String idEvento;
    private String idBanda;
    private String fotoBanda;
    private String nomeBanda;
    private String dataEvento;
    private String horaEvento;
    private String infoComplementar;
    private String precoIngresso;
    private String localEvento;
    private String bandaAbertura;
    private String image;

    public Event() {
    }

    public String getIdEvento() {
        return idEvento;
    }

    public void setIdEvento(String idEvento) {
        this.idEvento = idEvento;
    }

    public String getIdBanda() {
        return idBanda;
    }

    public void setIdBanda(String idBanda) {
        this.idBanda = idBanda;
    }

    public String getFotoBanda() {
        return fotoBanda;
    }

    public void setFotoBanda(String fotoBanda) {
        this.fotoBanda = fotoBanda;
    }

    public Event(String nomeBanda, String image) {
        this.nomeBanda = nomeBanda;
        this.image = image;
    }

    public String getDataEvento() {
        return dataEvento;
    }

    public void setDataEvento(String dataEvento) {
        this.dataEvento = dataEvento;
    }

    public String getHoraEvento() {
        return horaEvento;
    }

    public void setHoraEvento(String horaEvento) {
        this.horaEvento = horaEvento;
    }

    public String getInfoComplementar() {
        return infoComplementar;
    }

    public void setInfoComplementar(String infoComplementar) {
        this.infoComplementar = infoComplementar;
    }

    public String getPrecoIngresso() {
        return precoIngresso;
    }

    public void setPrecoIngresso(String precoIngresso) {
        this.precoIngresso = precoIngresso;
    }

    public String getLocalEvento() {
        return localEvento;
    }

    public void setLocalEvento(String localEvento) {
        this.localEvento = localEvento;
    }

    public String getBandaAbertura() {
        return bandaAbertura;
    }

    public void setBandaAbertura(String bandaAbertura) {
        this.bandaAbertura = bandaAbertura;
    }

    public String getNomeBanda() {
        return nomeBanda;
    }

    public void setNomeBanda(String nomeBanda) {
        this.nomeBanda = nomeBanda;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    @Override
    public int compareTo(@NonNull Event anotherEvent) {
        return this.getNomeBanda().compareTo(anotherEvent.getNomeBanda());
    }
}
