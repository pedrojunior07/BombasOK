/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package Model;

import java.io.Serializable;
import java.util.Date;
import javax.swing.ImageIcon;

/**
 *
 * @author Heisenberg
 */
public class ProdutoServico implements Serializable {
    private String descricao;
    private String tipo; //produto ou servico
    private double preco;
    private String unidadeDeMedida;
    private double quantidadeStock;
    ImageIcon imagem;
    private Date dataValidade;
    private double discontoPorcentagem; 
    public ImageIcon getImagem() {
        return imagem;
    }

    public double getDiscontoPorcentagem() {
        return discontoPorcentagem;
    }

    public void setDiscontoPorcentagem(double discontoPorcentagem) {
        this.discontoPorcentagem = discontoPorcentagem;
    }

    public void setImagem(ImageIcon imagem) {
        this.imagem = imagem;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public double getPreco() {
        return preco;
    }

    public void setPreco(double preco) {
        this.preco = preco;
    }

    public String getUnidadeDeMedida() {
        return unidadeDeMedida;
    }

    public void setUnidadeDeMedida(String unidadeDeMedida) {
        this.unidadeDeMedida = unidadeDeMedida;
    }

    public double getQuantidadeStock() {
        return quantidadeStock;
    }

    public void setQuantidadeStock(double quantidadeStock) {
        this.quantidadeStock = quantidadeStock;
    }

    public Date getDataValidade() {
        return dataValidade;
    }

    public void setDataValidade(Date dataValidade) {
        this.dataValidade = dataValidade;
    }
    
}
