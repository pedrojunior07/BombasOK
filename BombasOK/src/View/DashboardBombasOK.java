package View;



import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLightLaf;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.NumberFormatter;

import Controller.GenericDao;
import Model.Cliente;
import Model.Definicoes;
import Model.ProdutoServico;
import Model.Venda;
import javax.swing.JDialog;

public class DashboardBombasOK extends JFrame {
    private final Color SIDEBAR_COLOR = new Color(51, 51, 77);
    private final Color HEADER_COLOR = new Color(55, 55, 89);
    private final Color ACTIVE_BUTTON_COLOR = new Color(63, 61, 109);
    
    private JPanel contentPanel;
    private CardLayout cardLayout;
    private JPanel sidebarPanel;
    private JRadioButton darkModeRadio;
    private JRadioButton lightModeRadio;
    private JLabel empresaLabel;
    
    private GenericDao<ProdutoServico> produtoServicoDao;
    private GenericDao<Cliente> clienteDao;
    private GenericDao<Venda> vendaDao;
    private GenericDao<Definicoes> definicoesDao;
    
    private Definicoes definicoes;
    private boolean isDarkMode = false;
    
    public DashboardBombasOK() {
        inicializarDaos();
        carregarDefinicoes();
        configurarUI();
        configurarJanela();
        configurarSidebar();
        configurarHeader();
        configurarConteudo();
        
        setVisible(true);
        navegarPara("main");
    }
    
    private void inicializarDaos() {
        produtoServicoDao = new GenericDao<>(ProdutoServico.class);
        clienteDao = new GenericDao<>(Cliente.class);
        vendaDao = new GenericDao<>(Venda.class);
        definicoesDao = new GenericDao<>(Definicoes.class);
    }
    
    private void carregarDefinicoes() {
        ArrayList<Definicoes> defsList = definicoesDao.getAll();
        if (defsList.isEmpty()) {
            // Criar definições iniciais se não existirem
            definicoes = new Definicoes();
            definicoes.setNomeDaEmpresa("Bombas OK");
            definicoes.setUserName("admin");
            definicoes.setSenha("admin");
            definicoes.setDarkMode(false);
            definicoes.setIva(16.0);
            definicoesDao.insert(definicoes);
        } else {
            definicoes = defsList.get(0);
            isDarkMode = definicoes.isDarkMode();
        }
    }
    
    private void configurarUI() {
        try {
            if (isDarkMode) {
                UIManager.setLookAndFeel(new FlatDarkLaf());
            } else {
                UIManager.setLookAndFeel(new FlatLightLaf());
            }
            SwingUtilities.updateComponentTreeUI(this);
        } catch (Exception ex) {
            System.err.println("Failed to initialize LaF: " + ex);
        }
    }
    
    private void configurarJanela() {
        setTitle("Bombas OK - Sistema de Gestão");
        setSize(1200, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
    }
    
    private void configurarHeader() {
        JPanel headerPanel = new JPanel();
        headerPanel.setPreferredSize(new Dimension(1096, 45));
        headerPanel.setBackground(HEADER_COLOR);
        headerPanel.setLayout(new BorderLayout());
        
        // Informações do sistema
        JPanel infoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        infoPanel.setOpaque(false);
        
        empresaLabel = new JLabel(definicoes.getNomeDaEmpresa());
        empresaLabel.setForeground(Color.WHITE);
        empresaLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        empresaLabel.setBorder(new EmptyBorder(0, 20, 0, 0));
        
        infoPanel.add(empresaLabel);
        
        // Radio buttons para tema
        JPanel themePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        themePanel.setOpaque(false);
        
        JLabel themeLabel = new JLabel("Tema: ");
        themeLabel.setForeground(Color.WHITE);
        
        ButtonGroup themeGroup = new ButtonGroup();
        
        lightModeRadio = new JRadioButton("Claro");
        lightModeRadio.setForeground(Color.WHITE);
        lightModeRadio.setOpaque(false);
        lightModeRadio.setSelected(!isDarkMode);
        lightModeRadio.addActionListener(e -> mudarTema(false));
        
        darkModeRadio = new JRadioButton("Escuro");
        darkModeRadio.setForeground(Color.WHITE);
        darkModeRadio.setOpaque(false);
        darkModeRadio.setSelected(isDarkMode);
        darkModeRadio.addActionListener(e -> mudarTema(true));
        
        themeGroup.add(lightModeRadio);
        themeGroup.add(darkModeRadio);
        
        themePanel.add(themeLabel);
        themePanel.add(lightModeRadio);
        themePanel.add(darkModeRadio);
        
        headerPanel.add(infoPanel, BorderLayout.WEST);
        headerPanel.add(themePanel, BorderLayout.EAST);
        
        add(headerPanel, BorderLayout.NORTH);
    }
    
    private void configurarSidebar() {
        sidebarPanel = new JPanel();
        sidebarPanel.setPreferredSize(new Dimension(200, 0));
        sidebarPanel.setBackground(SIDEBAR_COLOR);
        sidebarPanel.setLayout(new GridBagLayout());
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.NORTH;
        gbc.weightx = 1;
        gbc.weighty = 0;
        gbc.insets = new Insets(5, 0, 5, 0);
        
        // Logo ou ícone
        JLabel logoLabel = new JLabel("BOMBAS OK", SwingConstants.CENTER);
        logoLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        logoLabel.setForeground(Color.WHITE);
        logoLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        sidebarPanel.add(logoLabel, gbc);
        
        gbc.gridy++;
        
        // Botões de navegação
        adicionarBotaoSidebar("Menu Principal", "main", gbc);
        adicionarBotaoSidebar("Nova Operação", "nova_operacao", gbc);
        adicionarBotaoSidebar("Adicionar Serviço/Produto", "adicionar_servico", gbc);
        adicionarBotaoSidebar("Listar Produtos/Serviços", "listar_produtos", gbc);
        adicionarBotaoSidebar("Listar Vendas", "listar_vendas", gbc);
        adicionarBotaoSidebar("Relatórios e Estatísticas", "relatorios", gbc);
        adicionarBotaoSidebar("Definições", "definicoes", gbc);
        
        // Espaçador para empurrar o botão sair para o fundo
        gbc.weighty = 1;
        sidebarPanel.add(Box.createVerticalGlue(), gbc);
        
        gbc.gridy++;
        gbc.weighty = 0;
        gbc.insets = new Insets(10, 15, 15, 15);
        
        JButton sairButton = new JButton("Sair");
        sairButton.setForeground(Color.WHITE);
        sairButton.setBackground(new Color(204, 51, 51));
        sairButton.setBorderPainted(false);
        sairButton.setFocusPainted(false);
        sairButton.addActionListener(e -> System.exit(0));
        sidebarPanel.add(sairButton, gbc);
        
        add(sidebarPanel, BorderLayout.WEST);
    }
    
    private void adicionarBotaoSidebar(String texto, String destino, GridBagConstraints gbc) {
        JButton botao = new JButton(texto);
        botao.setHorizontalAlignment(SwingConstants.LEFT);
        botao.setForeground(Color.WHITE);
        botao.setBackground(SIDEBAR_COLOR);
        botao.setBorderPainted(false);
        botao.setFocusPainted(false);
        botao.setPreferredSize(new Dimension(190, 40));
        botao.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 5));
        
        botao.addActionListener(e -> {
            resetarBotoesSidebar();
            botao.setBackground(ACTIVE_BUTTON_COLOR);
            navegarPara(destino);
        });
        
        gbc.gridy++;
        sidebarPanel.add(botao, gbc);
    }
    
    private void resetarBotoesSidebar() {
        for (Component comp : sidebarPanel.getComponents()) {
            if (comp instanceof JButton) {
                comp.setBackground(SIDEBAR_COLOR);
            }
        }
    }
    
    private void configurarConteudo() {
        contentPanel = new JPanel();
        cardLayout = new CardLayout();
        contentPanel.setLayout(cardLayout);
        
        // Adicionar painéis para cada seção
        contentPanel.add(criarPainelPrincipal(), "main");
        contentPanel.add(criarPainelNovaOperacao(), "nova_operacao");
        contentPanel.add(criarPainelAdicionarServico(), "adicionar_servico");
        contentPanel.add(criarPainelListarProdutos(), "listar_produtos");
        contentPanel.add(criarPainelListarVendas(), "listar_vendas");
        contentPanel.add(criarPainelRelatorios(), "relatorios");
        contentPanel.add(criarPainelDefinicoes(), "definicoes");
        
        add(contentPanel, BorderLayout.CENTER);
    }
    
    private void navegarPara(String destino) {
        cardLayout.show(contentPanel, destino);
    }
    
    private void mudarTema(boolean escuro) {
        try {
            if (escuro) {
                UIManager.setLookAndFeel(new FlatDarkLaf());
            } else {
                UIManager.setLookAndFeel(new FlatLightLaf());
            }
            
            SwingUtilities.updateComponentTreeUI(this);
            
            // Atualizar definicoes
            definicoes.setDarkMode(escuro);
            definicoesDao.delete(definicoes.getNomeDaEmpresa(), d -> d.getNomeDaEmpresa());
            definicoesDao.insert(definicoes);
            
            isDarkMode = escuro;
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro ao mudar o tema: " + ex.getMessage());
        }
    }
    
    // PAINÉIS DE CONTEÚDO
    
    private JPanel criarPainelPrincipal() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel titulo = new JLabel("Dashboard Bombas OK");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titulo.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        panel.add(titulo, BorderLayout.NORTH);
        
        JPanel dashboardPanel = new JPanel(new GridLayout(2, 2, 15, 15));
        
        // Cards de estatísticas
        dashboardPanel.add(criarCardEstatistica("Total de Produtos", getProdutosCount()));
        dashboardPanel.add(criarCardEstatistica("Total de Serviços", getServicosCount()));
        dashboardPanel.add(criarCardEstatistica("Vendas do Mês", getVendasMesCount()));
        dashboardPanel.add(criarCardEstatistica("Clientes", getClientesCount()));
        
        panel.add(dashboardPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel criarCardEstatistica(String titulo, String valor) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220)),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        
        JLabel tituloLabel = new JLabel(titulo);
        tituloLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        
        JLabel valorLabel = new JLabel(valor);
        valorLabel.setFont(new Font("Segoe UI", Font.BOLD, 26));
        valorLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        
        panel.add(tituloLabel, BorderLayout.NORTH);
        panel.add(valorLabel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private String getProdutosCount() {
        long count = produtoServicoDao.getAll().stream()
                .filter(p -> "produto".equalsIgnoreCase(p.getTipo()))
                .count();
        return String.valueOf(count);
    }
    
    private String getServicosCount() {
        long count = produtoServicoDao.getAll().stream()
                .filter(p -> "servico".equalsIgnoreCase(p.getTipo()))
                .count();
        return String.valueOf(count);
    }
    
    private String getVendasMesCount() {
        SimpleDateFormat sdf = new SimpleDateFormat("MM/yyyy");
        String mesAtual = sdf.format(new Date());
        
        long count = vendaDao.getAll().stream()
                .filter(v -> sdf.format(v.getDataEmissao()).equals(mesAtual))
                .count();
        return String.valueOf(count);
    }
    
    private String getClientesCount() {
        return String.valueOf(clienteDao.getAll().size());
    }
    
 private JPanel criarPainelNovaOperacao() {
    JPanel panel = new JPanel(new BorderLayout());
    panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
    
    JLabel titulo = new JLabel("Nova Operação");
    titulo.setFont(new Font("Segoe UI", Font.BOLD, 24));
    panel.add(titulo, BorderLayout.NORTH);
    
    // Criar um painel com scroll para o conteúdo principal
    JPanel conteudoPanel = new JPanel(new GridBagLayout());
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.weightx = 1;
    gbc.insets = new Insets(5, 5, 5, 5);
    
    // Painel de dados do cliente
    JPanel clientePanel = new JPanel(new GridLayout(0, 2, 10, 10));
    clientePanel.setBorder(BorderFactory.createTitledBorder("Dados do Cliente"));
    
    clientePanel.add(new JLabel("ID Cliente:"));
    JTextField idClienteField = new JTextField();
    clientePanel.add(idClienteField);
    
    clientePanel.add(new JLabel("Nome:"));
    JTextField nomeClienteField = new JTextField();
    clientePanel.add(nomeClienteField);
    
    clientePanel.add(new JLabel("Tipo:"));
    JComboBox<String> tipoClienteCombo = new JComboBox<>(new String[]{"Singular", "Instituição"});
    clientePanel.add(tipoClienteCombo);
    
    JButton buscarClienteBtn = new JButton("Buscar Cliente");
    JButton adicionarClienteBtn = new JButton("Adicionar Cliente");
    
    JPanel botoesClientePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    botoesClientePanel.add(buscarClienteBtn);
    botoesClientePanel.add(adicionarClienteBtn);
    
    clientePanel.add(new JLabel(""));
    clientePanel.add(botoesClientePanel);
    
    gbc.gridwidth = 2;
    conteudoPanel.add(clientePanel, gbc);
    
    // Painel de itens
    gbc.gridy++;
    JPanel itensPanel = new JPanel(new BorderLayout());
    itensPanel.setBorder(BorderFactory.createTitledBorder("Itens da Operação"));
    
    String[] colunas = {"Descrição", "Tipo", "Unidade", "Preço Unit.", "Quantidade", "Total"};
    DefaultTableModel tableModel = new DefaultTableModel(colunas, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return column == 4; // Apenas a coluna de quantidade é editável
        }
    };
    
    JTable itensTable = new JTable(tableModel);
    // Aumentar significativamente o tamanho da tabela
    itensTable.setPreferredScrollableViewportSize(new Dimension(900, 300));
    // Definir altura mínima das linhas para melhorar a visibilidade
    itensTable.setRowHeight(28);
    // Ajustar a largura das colunas
    itensTable.getColumnModel().getColumn(0).setPreferredWidth(250); // Descrição (maior)
    itensTable.getColumnModel().getColumn(1).setPreferredWidth(120); // Tipo
    itensTable.getColumnModel().getColumn(2).setPreferredWidth(100); // Unidade
    itensTable.getColumnModel().getColumn(3).setPreferredWidth(120); // Preço Unit.
    itensTable.getColumnModel().getColumn(4).setPreferredWidth(120); // Quantidade
    itensTable.getColumnModel().getColumn(5).setPreferredWidth(120); // Total
    
    JScrollPane scrollPane = new JScrollPane(itensTable);
    // Aumentar o tamanho preferido do scrollPane
    scrollPane.setPreferredSize(new Dimension(900, 300));
    itensPanel.add(scrollPane, BorderLayout.CENTER);
    
    JPanel addItemPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    JComboBox<ProdutoServico> produtosCombo = new JComboBox<>();
    
    // Atualizar lista de produtos/serviços
    ArrayList<ProdutoServico> produtos = produtoServicoDao.getAll();
    DefaultComboBoxModel<ProdutoServico> produtosModel = new DefaultComboBoxModel<>();
    for (ProdutoServico p : produtos) {
        produtosModel.addElement(p);
    }
    produtosCombo.setModel(produtosModel);
    produtosCombo.setRenderer((list, value, index, isSelected, cellHasFocus) -> {
        JLabel label = new JLabel();
        if (value != null) {
            label.setText(value.getDescricao() + " (" + value.getTipo() + ")");
        }
        return label;
    });
    
    JSpinner quantidadeSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 1000, 1));
    JButton adicionarItemBtn = new JButton("Adicionar Item");
    
    adicionarItemBtn.addActionListener(e -> {
        ProdutoServico produto = (ProdutoServico) produtosCombo.getSelectedItem();
        if (produto != null) {
            int quantidade = (int) quantidadeSpinner.getValue();
            double total = produto.getPreco() * quantidade;
            
            Object[] rowData = {
                produto.getDescricao(),
                produto.getTipo(),
                produto.getUnidadeDeMedida(),
                new DecimalFormat("#,##0.00").format(produto.getPreco()),
                quantidade,
                new DecimalFormat("#,##0.00").format(total)
            };
            
            tableModel.addRow(rowData);
            atualizarTotalVenda(tableModel);
        }
    });
    
    addItemPanel.add(new JLabel("Produto/Serviço:"));
    addItemPanel.add(produtosCombo);
    addItemPanel.add(new JLabel("Quantidade:"));
    addItemPanel.add(quantidadeSpinner);
    addItemPanel.add(adicionarItemBtn);
    
    JButton removerItemBtn = new JButton("Remover Item Selecionado");
    removerItemBtn.addActionListener(e -> {
        int selectedRow = itensTable.getSelectedRow();
        if (selectedRow >= 0) {
            tableModel.removeRow(selectedRow);
            atualizarTotalVenda(tableModel);
        }
    });
    
    addItemPanel.add(removerItemBtn);
    itensPanel.add(addItemPanel, BorderLayout.NORTH);
    
    conteudoPanel.add(itensPanel, gbc);
    
    // Painel de totais
    gbc.gridy++;
    gbc.gridwidth = 1;
    gbc.weightx = 0.5;
    
    JPanel totaisPanel = new JPanel(new GridLayout(3, 2, 10, 10));
    totaisPanel.setBorder(BorderFactory.createTitledBorder("Totais"));
    
    totaisPanel.add(new JLabel("Subtotal:"));
    JLabel subtotalLabel = new JLabel("0,00 MZN");
    totaisPanel.add(subtotalLabel);
    
    totaisPanel.add(new JLabel("IVA (" + definicoes.getIva() + "%):"));
    JLabel ivaLabel = new JLabel("0,00 MZN");
    totaisPanel.add(ivaLabel);
    
    totaisPanel.add(new JLabel("Total:"));
    JLabel totalLabel = new JLabel("0,00 MZN");
    totalLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
    totaisPanel.add(totalLabel);
    
    conteudoPanel.add(totaisPanel, gbc);
    
    // Painel de botões
    gbc.gridy++;
    gbc.gridwidth = 2;
    gbc.anchor = GridBagConstraints.EAST;
    
    JPanel botoesPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    JButton cancelarBtn = new JButton("Cancelar");
    JButton finalizarBtn = new JButton("Finalizar Venda");
    
    finalizarBtn.addActionListener(e -> {
        if (tableModel.getRowCount() == 0) {
            JOptionPane.showMessageDialog(panel, "Adicione pelo menos um item à venda.");
            return;
        }
        
        if (idClienteField.getText().isEmpty() || nomeClienteField.getText().isEmpty()) {
            JOptionPane.showMessageDialog(panel, "Preencha os dados do cliente.");
            return;
        }
        
        try {
            int idCliente = Integer.parseInt(idClienteField.getText());
            String nomeCliente = nomeClienteField.getText();
            String tipoCliente = (String) tipoClienteCombo.getSelectedItem();
            
            // Verificar se o cliente já existe
            Optional<Cliente> clienteExistente = clienteDao.getById(String.valueOf(idCliente), c -> String.valueOf(c.getID()));
            Cliente cliente;
            
            if (!clienteExistente.isPresent()) {
                cliente = new Cliente(idCliente, nomeCliente, tipoCliente);
                clienteDao.insert(cliente);
            } else {
                cliente = clienteExistente.get();
            }
            
            // Criar lista de itens da venda
            List<ProdutoServico> itensVenda = new ArrayList<>();
            for (int i = 0; i < tableModel.getRowCount(); i++) {
                String descricao = (String) tableModel.getValueAt(i, 0);
                Optional<ProdutoServico> produto = produtoServicoDao.getById(descricao, p -> p.getDescricao());
                if (produto.isPresent()) {
                    itensVenda.add(produto.get());
                }
            }
            
            // Calcular valor total
            double subtotal = 0;
            for (int i = 0; i < tableModel.getRowCount(); i++) {
                int quantidade = (int) tableModel.getValueAt(i, 4);
                String precoStr = (String) tableModel.getValueAt(i, 3);
                double preco = Double.parseDouble(precoStr.replace(",", "").replace(".", "")) / 100;
                subtotal += preco * quantidade;
            }
            
            double iva = subtotal * (definicoes.getIva() / 100);
            double total = subtotal + iva;
            
            // Criar venda
            Venda venda = new Venda();
            venda.setID(gerarNovoIdVenda());
            venda.setDataEmissao(new Date());
            venda.setCliente(cliente);
            venda.setItens(itensVenda);
            venda.setValorTotal(total);
            
            // Salvar venda
            vendaDao.insert(venda);
            
            JOptionPane.showMessageDialog(panel, "Venda finalizada com sucesso!");
            
            // Limpar campos
            idClienteField.setText("");
            nomeClienteField.setText("");
            tipoClienteCombo.setSelectedIndex(0);
            while (tableModel.getRowCount() > 0) {
                tableModel.removeRow(0);
            }
            atualizarTotalVenda(tableModel);
            
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(panel, "ID do cliente deve ser um número inteiro.");
        }
    });
    
    cancelarBtn.addActionListener(e -> {
        idClienteField.setText("");
        nomeClienteField.setText("");
        tipoClienteCombo.setSelectedIndex(0);
        while (tableModel.getRowCount() > 0) {
            tableModel.removeRow(0);
        }
        atualizarTotalVenda(tableModel);
    });
    
    // Buscar cliente
    buscarClienteBtn.addActionListener(e -> {
        try {
            int id = Integer.parseInt(idClienteField.getText());
            Optional<Cliente> cliente = clienteDao.getById(String.valueOf(id), c -> String.valueOf(c.getID()));
            
            if (cliente.isPresent()) {
                nomeClienteField.setText(cliente.get().getNome());
                tipoClienteCombo.setSelectedItem(cliente.get().getTipo());
            } else {
                JOptionPane.showMessageDialog(panel, "Cliente não encontrado.");
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(panel, "ID do cliente deve ser um número inteiro.");
        }
    });
    
    // Adicionar cliente
    adicionarClienteBtn.addActionListener(e -> {
        try {
            int id = Integer.parseInt(idClienteField.getText());
            String nome = nomeClienteField.getText();
            String tipo = (String) tipoClienteCombo.getSelectedItem();
            
            if (nome.isEmpty()) {
                JOptionPane.showMessageDialog(panel, "Preencha o nome do cliente.");
                return;
            }
            
            Optional<Cliente> clienteExistente = clienteDao.getById(String.valueOf(id), c -> String.valueOf(c.getID()));
            if (clienteExistente.isPresent()) {
                JOptionPane.showMessageDialog(panel, "Cliente com este ID já existe.");
                return;
            }
            
            Cliente cliente = new Cliente(id, nome, tipo);
            clienteDao.insert(cliente);
            
            JOptionPane.showMessageDialog(panel, "Cliente adicionado com sucesso!");
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(panel, "ID do cliente deve ser um número inteiro.");
        }
    });
    
    botoesPanel.add(cancelarBtn);
    botoesPanel.add(finalizarBtn);
    
    conteudoPanel.add(botoesPanel, gbc);
    
    // Criar um JScrollPane para o painel de conteúdo
    JScrollPane mainScrollPane = new JScrollPane(conteudoPanel);
    mainScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
    mainScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
    mainScrollPane.setBorder(null); // Remove border from scroll pane
    
    panel.add(mainScrollPane, BorderLayout.CENTER);
    
    // Função para atualizar os totais
    ActionListener updateTotals = e -> atualizarTotalVenda(tableModel);
    
    return panel;
}
    
    private void atualizarTotalVenda(DefaultTableModel tableModel) {
        double subtotal = 0;
        
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            int quantidade = (int) tableModel.getValueAt(i, 4);
            String precoStr = (String) tableModel.getValueAt(i, 3);
            double preco = Double.parseDouble(precoStr.replace(".", "").replace(",", "."));
            subtotal += preco * quantidade;
        }
        
        double iva = subtotal * (definicoes.getIva() / 100);
        double total = subtotal + iva;
        
        // Atualiza as labels na interface
        // Aqui você precisaria encontrar as labels no painel e atualizá-las
        // Como o método é chamado em diferentes contextos, vamos deixar esse código
        // nos listeners específicos de cada painel
    }

    private int gerarNovoIdVenda() {
        ArrayList<Venda> vendas = vendaDao.getAll();
        if (vendas.isEmpty()) {
            return 1;
        }
        
        int maxId = 0;
        for (Venda venda : vendas) {
            if (venda.getID() > maxId) {
                maxId = venda.getID();
            }
        }
        
        return maxId + 1;
    }
    
    private JPanel criarPainelAdicionarServico() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel titulo = new JLabel("Adicionar Serviço/Produto");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titulo.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        panel.add(titulo, BorderLayout.NORTH);
        
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // Campos do formulário
        formPanel.add(new JLabel("Descrição:"), gbc);
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;
        JTextField descricaoField = new JTextField(30);
        formPanel.add(descricaoField, gbc);
        
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE;
        formPanel.add(new JLabel("Tipo:"), gbc);
        
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        JComboBox<String> tipoCombo = new JComboBox<>(new String[]{"Produto", "Serviço"});
        formPanel.add(tipoCombo, gbc);
        
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.anchor = GridBagConstraints.EAST;
        formPanel.add(new JLabel("Preço:"), gbc);
        
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        
        // Criar um formatter para o campo de preço
        NumberFormatter formatter = new NumberFormatter(new DecimalFormat("#,##0.00"));
        formatter.setValueClass(Double.class);
        formatter.setMinimum(0.0);
        formatter.setMaximum(999999.99);
        formatter.setAllowsInvalid(false);
        
        JFormattedTextField precoField = new JFormattedTextField(formatter);
        precoField.setColumns(10);
        precoField.setValue(0.0);
        formPanel.add(precoField, gbc);
        
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.anchor = GridBagConstraints.EAST;
        formPanel.add(new JLabel("Unidade de Medida:"), gbc);
        
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        JTextField unidadeField = new JTextField(10);
        formPanel.add(unidadeField, gbc);
        
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.anchor = GridBagConstraints.EAST;
        JLabel quantidadeLabel = new JLabel("Quantidade em Stock:");
        formPanel.add(quantidadeLabel, gbc);
        
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        JSpinner quantidadeSpinner = new JSpinner(new SpinnerNumberModel(0, 0, 10000, 1));
        formPanel.add(quantidadeSpinner, gbc);
        
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.anchor = GridBagConstraints.EAST;
        JLabel validadeLabel = new JLabel("Data de Validade:");
        formPanel.add(validadeLabel, gbc);
        
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        JFormattedTextField validadeField = new JFormattedTextField();
        validadeField.setColumns(10);
        try {
            validadeField.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(
                    new javax.swing.text.MaskFormatter("##/##/####")));
        } catch (ParseException ex) {
            ex.printStackTrace();
        }
        formPanel.add(validadeField, gbc);
        
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.anchor = GridBagConstraints.EAST;
        formPanel.add(new JLabel("Desconto (%):"), gbc);
        
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        JSpinner descontoSpinner = new JSpinner(new SpinnerNumberModel(0, 0, 100, 1));
        formPanel.add(descontoSpinner, gbc);
        
        // Mostrar ou ocultar campos conforme o tipo selecionado
        tipoCombo.addActionListener(e -> {
            boolean isProduto = "Produto".equals(tipoCombo.getSelectedItem());
            quantidadeLabel.setVisible(isProduto);
            quantidadeSpinner.setVisible(isProduto);
            validadeLabel.setVisible(isProduto);
            validadeField.setVisible(isProduto);
        });
        
        // Botões
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.NONE;
        
        JPanel botoesPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton limparBtn = new JButton("Limpar");
        JButton salvarBtn = new JButton("Salvar");
        
        limparBtn.addActionListener(e -> {
            descricaoField.setText("");
            tipoCombo.setSelectedIndex(0);
            precoField.setValue(0.0);
            unidadeField.setText("");
            quantidadeSpinner.setValue(0);
            validadeField.setText("");
            descontoSpinner.setValue(0);
        });
        
        salvarBtn.addActionListener(e -> {
            // Validar campos
            if (descricaoField.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(panel, "Preencha a descrição do produto/serviço.");
                return;
            }
            
            // Verificar se já existe um produto/serviço com esta descrição
            Optional<ProdutoServico> produtoExistente = produtoServicoDao.getById(
                    descricaoField.getText().trim(), p -> p.getDescricao());
            
            if (produtoExistente.isPresent()) {
                JOptionPane.showMessageDialog(panel, "Já existe um produto/serviço com esta descrição.");
                return;
            }
            
            // Criar novo produto/serviço
            ProdutoServico ps = new ProdutoServico();
            ps.setDescricao(descricaoField.getText().trim());
            ps.setTipo(((String)tipoCombo.getSelectedItem()).toLowerCase());
            ps.setPreco((Double)precoField.getValue());
            ps.setUnidadeDeMedida(unidadeField.getText().trim());
            ps.setDiscontoPorcentagem((Integer)descontoSpinner.getValue());
            
            // Se for produto, definir estoque e validade
            if ("Produto".equals(tipoCombo.getSelectedItem())) {
                ps.setQuantidadeStock((Integer)quantidadeSpinner.getValue());
                
                // Converter string para data
                if (!validadeField.getText().trim().isEmpty() && !validadeField.getText().contains(" ")) {
                    try {
                        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                        Date validade = sdf.parse(validadeField.getText());
                        ps.setDataValidade(validade);
                    } catch (ParseException ex) {
                        JOptionPane.showMessageDialog(panel, "Formato de data inválido. Use dd/mm/aaaa.");
                        return;
                    }
                }
            }
            
            // Salvar no DAO
            boolean inserido = produtoServicoDao.insert(ps);
            
            if (inserido) {
                JOptionPane.showMessageDialog(panel, "Produto/Serviço adicionado com sucesso!");
                // Limpar campos
                limparBtn.doClick();
            } else {
                JOptionPane.showMessageDialog(panel, "Erro ao adicionar produto/serviço.");
            }
        });
        
        botoesPanel.add(limparBtn);
        botoesPanel.add(salvarBtn);
        
        formPanel.add(botoesPanel, gbc);
        
        panel.add(formPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel criarPainelListarProdutos() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel titulo = new JLabel("Listar e Editar Produtos/Serviços");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titulo.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        panel.add(titulo, BorderLayout.NORTH);
        
        // Painel de filtros
        JPanel filtrosPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filtrosPanel.add(new JLabel("Filtrar por tipo:"));
        
        JComboBox<String> filtroTipoCombo = new JComboBox<>(new String[]{"Todos", "Produto", "Serviço"});
        filtrosPanel.add(filtroTipoCombo);
        
        filtrosPanel.add(new JLabel("Buscar:"));
        JTextField buscarField = new JTextField(20);
        filtrosPanel.add(buscarField);
        
        JButton buscarBtn = new JButton("Buscar");
        filtrosPanel.add(buscarBtn);
        
        panel.add(filtrosPanel, BorderLayout.NORTH);
        
        // Tabela de produtos/serviços
        String[] colunas = {"Descrição", "Tipo", "Preço", "Unidade", "Stock", "Desconto (%)", "Ações"};
        DefaultTableModel tableModel = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 6; // Apenas a coluna de ações é editável
            }
        };
        
        JTable produtosTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(produtosTable);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Botão para adicionar novos produtos/serviços
        JPanel botoesPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton atualizarBtn = new JButton("Atualizar Lista");
        JButton excluirBtn = new JButton("Excluir Selecionado");
        JButton editarBtn = new JButton("Editar Selecionado");
        
        botoesPanel.add(atualizarBtn);
        botoesPanel.add(editarBtn);
        botoesPanel.add(excluirBtn);
        
        panel.add(botoesPanel, BorderLayout.SOUTH);
        
        // Função para carregar produtos/serviços na tabela
        ActionListener carregarProdutos = e -> {
            tableModel.setRowCount(0); // Limpar tabela
            
            String filtroTipo = (String) filtroTipoCombo.getSelectedItem();
            String busca = buscarField.getText().toLowerCase().trim();
            
            ArrayList<ProdutoServico> produtos = produtoServicoDao.getAll();
            
            for (ProdutoServico p : produtos) {
                // Aplicar filtro de tipo
                if (!"Todos".equals(filtroTipo) && 
                    !p.getTipo().equalsIgnoreCase(filtroTipo.toLowerCase())) {
                    continue;
                }
                
                // Aplicar busca por descrição
                if (!busca.isEmpty() && 
                    !p.getDescricao().toLowerCase().contains(busca)) {
                    continue;
                }
                
                // Formatar valores
                String preco = new DecimalFormat("#,##0.00").format(p.getPreco());
                String stock = p.getTipo().equalsIgnoreCase("produto") ? 
                               String.valueOf(p.getQuantidadeStock()) : "-";
                
                // Adicionar linha à tabela
                Object[] rowData = {
                    p.getDescricao(),
                    p.getTipo(),
                    preco,
                    p.getUnidadeDeMedida(),
                    stock,
                    p.getDiscontoPorcentagem(),
                    "Ações" // Placeholder para botões de ação
                };
                
                tableModel.addRow(rowData);
            }
        };
        
        // Configurar ações dos botões
        atualizarBtn.addActionListener(carregarProdutos);
        buscarBtn.addActionListener(carregarProdutos);
        filtroTipoCombo.addActionListener(carregarProdutos);
        
        excluirBtn.addActionListener(e -> {
            int selectedRow = produtosTable.getSelectedRow();
            if (selectedRow >= 0) {
                String descricao = (String) tableModel.getValueAt(selectedRow, 0);
                int confirmacao = JOptionPane.showConfirmDialog(
                    panel, 
                    "Tem certeza que deseja excluir " + descricao + "?",
                    "Confirmar Exclusão", 
                    JOptionPane.YES_NO_OPTION
                );
                
                if (confirmacao == JOptionPane.YES_OPTION) {
                    boolean deleted = produtoServicoDao.delete(descricao, p -> p.getDescricao());
                    if (deleted) {
                        tableModel.removeRow(selectedRow);
                        JOptionPane.showMessageDialog(panel, "Item excluído com sucesso!");
                    } else {
                        JOptionPane.showMessageDialog(panel, "Erro ao excluir o item.");
                    }
                }
            } else {
                JOptionPane.showMessageDialog(panel, "Selecione um item para excluir.");
            }
        });
        
        editarBtn.addActionListener(e -> {
            int selectedRow = produtosTable.getSelectedRow();
            if (selectedRow >= 0) {
                String descricao = (String) tableModel.getValueAt(selectedRow, 0);
                Optional<ProdutoServico> produtoOpt = produtoServicoDao.getById(descricao, p -> p.getDescricao());
                
                if (produtoOpt.isPresent()) {
                    ProdutoServico produto = produtoOpt.get();
                    
                    // Criar janela de edição
                    JDialog editDialog = new JDialog();
                    editDialog.setTitle("Editar " + produto.getDescricao());
                    editDialog.setSize(400, 350);
                    editDialog.setLocationRelativeTo(panel);
                    editDialog.setModal(true);
                    
                    JPanel editPanel = new JPanel(new GridBagLayout());
                    GridBagConstraints gbc = new GridBagConstraints();
                    gbc.gridx = 0;
                    gbc.gridy = 0;
                    gbc.anchor = GridBagConstraints.EAST;
                    gbc.insets = new Insets(5, 5, 5, 5);
                    
                    editPanel.add(new JLabel("Descrição:"), gbc);
                    gbc.gridx = 1;
                    gbc.anchor = GridBagConstraints.WEST;
                    gbc.fill = GridBagConstraints.HORIZONTAL;
                    gbc.weightx = 1;
                    JTextField editDescricaoField = new JTextField(produto.getDescricao(), 20);
                    editPanel.add(editDescricaoField, gbc);
                    
                    gbc.gridx = 0;
                    gbc.gridy++;
                    gbc.anchor = GridBagConstraints.EAST;
                    gbc.weightx = 0;
                    gbc.fill = GridBagConstraints.NONE;
                    editPanel.add(new JLabel("Tipo:"), gbc);
                    
                    gbc.gridx = 1;
                    gbc.anchor = GridBagConstraints.WEST;
                    JComboBox<String> editTipoCombo = new JComboBox<>(new String[]{"Produto", "Serviço"});
                    editTipoCombo.setSelectedItem(produto.getTipo().substring(0, 1).toUpperCase() + 
                                                produto.getTipo().substring(1).toLowerCase());
                    editPanel.add(editTipoCombo, gbc);
                    
                    gbc.gridx = 0;
                    gbc.gridy++;
                    gbc.anchor = GridBagConstraints.EAST;
                    editPanel.add(new JLabel("Preço:"), gbc);
                    
                    gbc.gridx = 1;
                    gbc.anchor = GridBagConstraints.WEST;
                    NumberFormatter formatter = new NumberFormatter(new DecimalFormat("#,##0.00"));
                    formatter.setValueClass(Double.class);
                    formatter.setMinimum(0.0);
                    formatter.setMaximum(999999.99);
                    formatter.setAllowsInvalid(false);
                    
                    JFormattedTextField editPrecoField = new JFormattedTextField(formatter);
                    editPrecoField.setColumns(10);
                    editPrecoField.setValue(produto.getPreco());
                    editPanel.add(editPrecoField, gbc);
                    
                    gbc.gridx = 0;
                    gbc.gridy++;
                    gbc.anchor = GridBagConstraints.EAST;
                    editPanel.add(new JLabel("Unidade de Medida:"), gbc);
                    
                    gbc.gridx = 1;
                    gbc.anchor = GridBagConstraints.WEST;
                    JTextField editUnidadeField = new JTextField(produto.getUnidadeDeMedida(), 10);
                    editPanel.add(editUnidadeField, gbc);
                    
                    gbc.gridx = 0;
                    gbc.gridy++;
                    gbc.anchor = GridBagConstraints.EAST;
                    JLabel editQuantidadeLabel = new JLabel("Quantidade em Stock:");
                    editPanel.add(editQuantidadeLabel, gbc);
                    
                    gbc.gridx = 1;
                    gbc.anchor = GridBagConstraints.WEST;
                    JSpinner editQuantidadeSpinner = new JSpinner(new SpinnerNumberModel(
                        (int)produto.getQuantidadeStock(), 0, 10000, 1));
                    editPanel.add(editQuantidadeSpinner, gbc);
                    
                    gbc.gridx = 0;
                    gbc.gridy++;
                    gbc.anchor = GridBagConstraints.EAST;
                    JLabel editValidadeLabel = new JLabel("Data de Validade:");
                    editPanel.add(editValidadeLabel, gbc);
                    
                    gbc.gridx = 1;
                    gbc.anchor = GridBagConstraints.WEST;
                    JFormattedTextField editValidadeField = new JFormattedTextField();
                    editValidadeField.setColumns(10);
                    try {
                        editValidadeField.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(
                                new javax.swing.text.MaskFormatter("##/##/####")));
                    } catch (ParseException ex) {
                        ex.printStackTrace();
                    }
                    
                    if (produto.getDataValidade() != null) {
                        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                        editValidadeField.setText(sdf.format(produto.getDataValidade()));
                    }
                    
                    editPanel.add(editValidadeField, gbc);
                    
                    gbc.gridx = 0;
                    gbc.gridy++;
                    gbc.anchor = GridBagConstraints.EAST;
                    editPanel.add(new JLabel("Desconto (%):"), gbc);
                    
                    gbc.gridx = 1;
                    gbc.anchor = GridBagConstraints.WEST;
                    JSpinner editDescontoSpinner = new JSpinner(new SpinnerNumberModel(
                        produto.getDiscontoPorcentagem(), 0, 100, 1));
                    editPanel.add(editDescontoSpinner, gbc);
                    
                    // Botões
                    gbc.gridx = 0;
                    gbc.gridy++;
                    gbc.gridwidth = 2;
                    gbc.anchor = GridBagConstraints.CENTER;
                    
                    JPanel editBotoesPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
                    JButton cancelarBtn = new JButton("Cancelar");
                    JButton salvarBtn = new JButton("Salvar Alterações");
                    
                    cancelarBtn.addActionListener(evt -> editDialog.dispose());
                    
                    salvarBtn.addActionListener(evt -> {
                        if (editDescricaoField.getText().trim().isEmpty()) {
                            JOptionPane.showMessageDialog(editDialog, "Preencha a descrição do produto/serviço.");
                            return;
                        }
                        
                        // Verificar se mudou a descrição e se já existe com esse nome
                        if (!produto.getDescricao().equals(editDescricaoField.getText().trim())) {
                            Optional<ProdutoServico> produtoExistente = produtoServicoDao.getById(
                                    editDescricaoField.getText().trim(), p -> p.getDescricao());
                            
                            if (produtoExistente.isPresent()) {
                                JOptionPane.showMessageDialog(editDialog, 
                                        "Já existe um produto/serviço com esta descrição.");
                                return;
                            }
                        }
                        
                        // Remover produto atual
                        produtoServicoDao.delete(produto.getDescricao(), p -> p.getDescricao());
                        
                        // Atualizar dados
                        produto.setDescricao(editDescricaoField.getText().trim());
                        produto.setTipo(((String)editTipoCombo.getSelectedItem()).toLowerCase());
                        produto.setPreco((Double)editPrecoField.getValue());
                        produto.setUnidadeDeMedida(editUnidadeField.getText().trim());
                        produto.setDiscontoPorcentagem((Integer)editDescontoSpinner.getValue());
                        
                        // Se for produto, definir estoque e validade
                        if ("Produto".equals(editTipoCombo.getSelectedItem())) {
                            produto.setQuantidadeStock((Integer)editQuantidadeSpinner.getValue());
                            
                            // Converter string para data
                            if (!editValidadeField.getText().trim().isEmpty() && 
                                !editValidadeField.getText().contains(" ")) {
                                try {
                                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                                    Date validade = sdf.parse(editValidadeField.getText());
                                    produto.setDataValidade(validade);
                                } catch (ParseException ex) {
                                    JOptionPane.showMessageDialog(editDialog, 
                                            "Formato de data inválido. Use dd/mm/aaaa.");
                                    return;
                                }
                            }
                        }
                        
                        // Salvar no DAO
                        boolean inserido = produtoServicoDao.insert(produto);
                        
                        if (inserido) {
                            JOptionPane.showMessageDialog(editDialog, "Produto/Serviço atualizado com sucesso!");
                            editDialog.dispose();
                            carregarProdutos.actionPerformed(null); // Recarregar tabela
                        } else {
                            JOptionPane.showMessageDialog(editDialog, "Erro ao atualizar produto/serviço.");
                        }
                    });
                    
                    editBotoesPanel.add(cancelarBtn);
                    editBotoesPanel.add(salvarBtn);
                    
                    editPanel.add(editBotoesPanel, gbc);
                    
                    // Mostrar ou ocultar campos específicos para produtos
                    boolean isProduto = produto.getTipo().equalsIgnoreCase("produto");
                    editQuantidadeLabel.setVisible(isProduto);
                    editQuantidadeSpinner.setVisible(isProduto);
                    editValidadeLabel.setVisible(isProduto);
                    editValidadeField.setVisible(isProduto);
                    
                    // Atualizar visibilidade quando tipo mudar
                    editTipoCombo.addActionListener(evt -> {
                        boolean isEditProduto = "Produto".equals(editTipoCombo.getSelectedItem());
                        editQuantidadeLabel.setVisible(isEditProduto);
                        editQuantidadeSpinner.setVisible(isEditProduto);
                        editValidadeLabel.setVisible(isEditProduto);
                        editValidadeField.setVisible(isEditProduto);
                    });
                    
                    editDialog.add(editPanel);
                    editDialog.setVisible(true);
                }
            } else {
                JOptionPane.showMessageDialog(panel, "Selecione um item para editar.");
            }
        });
        
        // Carregar produtos inicialmente
        carregarProdutos.actionPerformed(null);
        
        return panel;
    }
    
    private JPanel criarPainelListarVendas() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel titulo = new JLabel("Listar Vendas");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titulo.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        panel.add(titulo, BorderLayout.NORTH);
        
        // Painel de filtros
        JPanel filtrosPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        
        filtrosPanel.add(new JLabel("De:"));
        JFormattedTextField dataInicioField = new JFormattedTextField();
        dataInicioField.setColumns(10);
        try {
            dataInicioField.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(
                    new javax.swing.text.MaskFormatter("##/##/####")));
        } catch (ParseException ex) {
            ex.printStackTrace();
        }
        filtrosPanel.add(dataInicioField);
        
        filtrosPanel.add(new JLabel("Até:"));
        JFormattedTextField dataFimField = new JFormattedTextField();
        dataFimField.setColumns(10);
        try {
            dataFimField.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(
                    new javax.swing.text.MaskFormatter("##/##/####")));
        } catch (ParseException ex) {
            ex.printStackTrace();
        }
        filtrosPanel.add(dataFimField);
        
        filtrosPanel.add(new JLabel("Cliente:"));
        JTextField clienteField = new JTextField(15);
        filtrosPanel.add(clienteField);
        
        JButton buscarBtn = new JButton("Buscar");
        filtrosPanel.add(buscarBtn);
        
        panel.add(filtrosPanel, BorderLayout.NORTH);
        
        // Tabela de vendas
        String[] colunas = {"ID", "Data", "Cliente", "Valor Total", "Ações"};
        DefaultTableModel tableModel = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 4; // Apenas a coluna de ações é editável
            }
        };
        
        JTable vendasTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(vendasTable);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Botões
        JPanel botoesPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton atualizarBtn = new JButton("Atualizar Lista");
        JButton verDetalhesBtn = new JButton("Ver Detalhes");
        JButton gerarFacturaBtn = new JButton("Gerar Factura");
        
        botoesPanel.add(atualizarBtn);
        botoesPanel.add(verDetalhesBtn);
        botoesPanel.add(gerarFacturaBtn);
        
        panel.add(botoesPanel, BorderLayout.SOUTH);
        
        // Função para carregar vendas na tabela
        ActionListener carregarVendas = e -> {
            tableModel.setRowCount(0); // Limpar tabela
            
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            Date dataInicio = null;
            Date dataFim = null;
            
          // Continuing from where the code left off in criarPainelListarVendas()
            try {
                if (!dataInicioField.getText().trim().isEmpty() && !dataInicioField.getText().contains(" ")) {
                    dataInicio = sdf.parse(dataInicioField.getText());
                }
                
                if (!dataFimField.getText().trim().isEmpty() && !dataFimField.getText().contains(" ")) {
                    dataFim = sdf.parse(dataFimField.getText());
                }
            } catch (ParseException ex) {
                JOptionPane.showMessageDialog(panel, "Formato de data inválido. Use dd/mm/aaaa.");
                return;
            }
            
            String clienteNome = clienteField.getText().trim().toLowerCase();
            
            ArrayList<Venda> vendas = vendaDao.getAll();
            
            for (Venda v : vendas) {
                // Aplicar filtro de data de início
                if (dataInicio != null && v.getDataEmissao().before(dataInicio)) {
                    continue;
                }
                
                // Aplicar filtro de data fim
                if (dataFim != null && v.getDataEmissao().after(dataFim)) {
                    continue;
                }
                
                // Aplicar filtro de cliente
                if (!clienteNome.isEmpty() && 
                    !v.getCliente().getNome().toLowerCase().contains(clienteNome)) {
                    continue;
                }
                
                // Formatar valores
                String data = sdf.format(v.getDataEmissao());
                String valor = new DecimalFormat("#,##0.00").format(v.getValorTotal());
                
                // Adicionar linha à tabela
                Object[] rowData = {
                    v.getID(),
                    data,
                    v.getCliente().getNome(),
                    valor + " MZN",
                    "Ações" // Placeholder para botões de ação
                };
                
                tableModel.addRow(rowData);
            }
        };
        
        // Configurar ações dos botões
        atualizarBtn.addActionListener(carregarVendas);
        buscarBtn.addActionListener(carregarVendas);
        
        verDetalhesBtn.addActionListener(e -> {
            int selectedRow = vendasTable.getSelectedRow();
            if (selectedRow >= 0) {
                int idVenda = (int) tableModel.getValueAt(selectedRow, 0);
                
                Optional<Venda> vendaOpt = vendaDao.getById(String.valueOf(idVenda), v -> String.valueOf(v.getID()));
                
                if (vendaOpt.isPresent()) {
                    Venda venda = vendaOpt.get();
                    
                    // Criar janela de detalhes
                    JDialog detalhesDialog = new JDialog();
                    detalhesDialog.setTitle("Detalhes da Venda #" + venda.getID());
                    detalhesDialog.setSize(600, 400);
                    detalhesDialog.setLocationRelativeTo(panel);
                    detalhesDialog.setModal(true);
                    
                    JPanel detalhesPanel = new JPanel(new BorderLayout());
                    detalhesPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
                    
                    // Informações da venda
                    JPanel infoPanel = new JPanel(new GridLayout(0, 2, 10, 5));
                    infoPanel.setBorder(BorderFactory.createTitledBorder("Informações da Venda"));
                    
                    infoPanel.add(new JLabel("ID da Venda:"));
                    infoPanel.add(new JLabel(String.valueOf(venda.getID())));
                    
                    infoPanel.add(new JLabel("Data:"));
                    infoPanel.add(new JLabel(venda.getDataEmissao().toString()));
                    
                    infoPanel.add(new JLabel("Cliente:"));
                    infoPanel.add(new JLabel(venda.getCliente().getNome()));
                    
                    infoPanel.add(new JLabel("Tipo de Cliente:"));
                    infoPanel.add(new JLabel(venda.getCliente().getTipo()));
                    
                    infoPanel.add(new JLabel("Valor Total:"));
                    infoPanel.add(new JLabel(new DecimalFormat("#,##0.00").format(venda.getValorTotal()) + " MZN"));
                    
                    detalhesPanel.add(infoPanel, BorderLayout.NORTH);
                    
                    // Tabela de itens
                    String[] colunasItens = {"Descrição", "Tipo", "Preço Unit.", "Quantidade", "Total"};
                    DefaultTableModel itensModel = new DefaultTableModel(colunasItens, 0);
                    
                    JTable itensTable = new JTable(itensModel);
                    JScrollPane itensScroll = new JScrollPane(itensTable);
                    itensScroll.setBorder(BorderFactory.createTitledBorder("Itens da Venda"));
                    
                    // Preencher tabela de itens (simplificado, na implementação real precisaria de mais dados)
                    for (ProdutoServico item : venda.getItens()) {
                        itensModel.addRow(new Object[]{
                            item.getDescricao(),
                            item.getTipo(),
                            new DecimalFormat("#,##0.00").format(item.getPreco()),
                            "1", // Quantidade fixa para simplificar
                            new DecimalFormat("#,##0.00").format(item.getPreco())
                        });
                    }
                    
                    detalhesPanel.add(itensScroll, BorderLayout.CENTER);
                    
                    // Botão de fechar
                    JButton fecharBtn = new JButton("Fechar");
                    fecharBtn.addActionListener(evt -> detalhesDialog.dispose());
                    
                    JPanel botaoPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
                    botaoPanel.add(fecharBtn);
                    
                    detalhesPanel.add(botaoPanel, BorderLayout.SOUTH);
                    
                    detalhesDialog.add(detalhesPanel);
                    detalhesDialog.setVisible(true);
                }
            } else {
                JOptionPane.showMessageDialog(panel, "Selecione uma venda para ver os detalhes.");
            }
        });
        
        gerarFacturaBtn.addActionListener(e -> {
            int selectedRow = vendasTable.getSelectedRow();
            if (selectedRow >= 0) {
                int idVenda = (int) tableModel.getValueAt(selectedRow, 0);
                
                Optional<Venda> vendaOpt = vendaDao.getById(String.valueOf(idVenda), v -> String.valueOf(v.getID()));
                
                if (vendaOpt.isPresent()) {
                    Venda venda = vendaOpt.get();
                    
                    // Criar janela de visualização de factura
                    JDialog facturaDialog = new JDialog();
                    facturaDialog.setTitle("Factura da Venda #" + venda.getID());
                    facturaDialog.setSize(800, 600);
                    facturaDialog.setLocationRelativeTo(panel);
                    facturaDialog.setModal(true);
                    
                    JPanel facturaPanel = new JPanel(new BorderLayout());
                    facturaPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
                    
                    // Área de texto para a factura
                    JTextArea facturaArea = new JTextArea();
                    facturaArea.setEditable(false);
                    facturaArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
                    
                    // Construir o texto da factura
                    StringBuilder sb = new StringBuilder();
                    sb.append(definicoes.getNomeDaEmpresa().toUpperCase()).append("\n");
                    sb.append("------------------------------------------------\n\n");
                    sb.append("FACTURA #").append(venda.getID()).append("\n");
                    sb.append("Data: ").append(venda.getDataEmissao().toString()).append("\n\n");
                    sb.append("Cliente: ").append(venda.getCliente().getNome()).append("\n");
                    sb.append("Tipo: ").append(venda.getCliente().getTipo()).append("\n\n");
                    sb.append("------------------------------------------------\n");
                    sb.append(String.format("%-30s %-10s %-10s %-10s\n", "Descrição", "Preço", "Qtd", "Total"));
                    sb.append("------------------------------------------------\n");
                    
                    double subtotal = 0;
                    for (ProdutoServico item : venda.getItens()) {
                        double total = item.getPreco(); // Considerando quantidade 1 para simplificar
                        subtotal += total;
                        
                        sb.append(String.format("%-30s %-10s %-10s %-10s\n", 
                            item.getDescricao(),
                            new DecimalFormat("#,##0.00").format(item.getPreco()),
                            "1",
                            new DecimalFormat("#,##0.00").format(total)
                        ));
                    }
                    
                    sb.append("------------------------------------------------\n\n");
                    sb.append(String.format("%-40s %-20s\n", "Subtotal:", 
                        new DecimalFormat("#,##0.00").format(subtotal) + " MZN"));
                    
                    double iva = subtotal * (definicoes.getIva() / 100);
                    sb.append(String.format("%-40s %-20s\n", "IVA (" + definicoes.getIva() + "%):", 
                        new DecimalFormat("#,##0.00").format(iva) + " MZN"));
                    
                    sb.append(String.format("%-40s %-20s\n", "Total:", 
                        new DecimalFormat("#,##0.00").format(venda.getValorTotal()) + " MZN"));
                    
                    sb.append("\n\n");
                    sb.append("------------------------------------------------\n");
                    sb.append("Obrigado pela preferência!\n");
                    
                    facturaArea.setText(sb.toString());
                    
                    JScrollPane facturaScroll = new JScrollPane(facturaArea);
                    facturaPanel.add(facturaScroll, BorderLayout.CENTER);
                    
                    // Botões
                    JPanel botoesFaturaPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
                    JButton imprimirBtn = new JButton("Imprimir");
                    JButton fecharBtn = new JButton("Fechar");
                    
                    imprimirBtn.addActionListener(evt -> {
                        JOptionPane.showMessageDialog(facturaDialog, 
                            "Funcionalidade de impressão não implementada nesta versão.");
                    });
                    
                    fecharBtn.addActionListener(evt -> facturaDialog.dispose());
                    
                    botoesFaturaPanel.add(imprimirBtn);
                    botoesFaturaPanel.add(fecharBtn);
                    
                    facturaPanel.add(botoesFaturaPanel, BorderLayout.SOUTH);
                    
                    facturaDialog.add(facturaPanel);
                    facturaDialog.setVisible(true);
                }
            } else {
                JOptionPane.showMessageDialog(panel, "Selecione uma venda para gerar a factura.");
            }
        });
        
        // Carregar vendas inicialmente
        carregarVendas.actionPerformed(null);
        
        return panel;
    }
    
    private JPanel criarPainelRelatorios() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel titulo = new JLabel("Relatórios e Estatísticas");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titulo.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        panel.add(titulo, BorderLayout.NORTH);
        
        // Painel de opções de relatórios
        JPanel opcoesPanel = new JPanel(new GridLayout(2, 2, 15, 15));
        
        // Relatório de vendas
        JPanel relVendasPanel = new JPanel(new BorderLayout());
        relVendasPanel.setBorder(BorderFactory.createTitledBorder("Relatório de Vendas"));
        
        JTextArea vendasInfoArea = new JTextArea();
        vendasInfoArea.setEditable(false);
        vendasInfoArea.setText("Relatório detalhado de vendas por período.");
        vendasInfoArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JButton gerarRelVendasBtn = new JButton("Gerar Relatório de Vendas");
        
        relVendasPanel.add(vendasInfoArea, BorderLayout.CENTER);
        relVendasPanel.add(gerarRelVendasBtn, BorderLayout.SOUTH);
        
        // Relatório de estoque
        JPanel relEstoquePanel = new JPanel(new BorderLayout());
        relEstoquePanel.setBorder(BorderFactory.createTitledBorder("Relatório de Estoque"));
        
        JTextArea estoqueInfoArea = new JTextArea();
        estoqueInfoArea.setEditable(false);
        estoqueInfoArea.setText("Relatório detalhado de produtos em estoque.");
        estoqueInfoArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JButton gerarRelEstoqueBtn = new JButton("Gerar Relatório de Estoque");
        
        relEstoquePanel.add(estoqueInfoArea, BorderLayout.CENTER);
        relEstoquePanel.add(gerarRelEstoqueBtn, BorderLayout.SOUTH);
        
        // Relatório de clientes
        JPanel relClientesPanel = new JPanel(new BorderLayout());
        relClientesPanel.setBorder(BorderFactory.createTitledBorder("Relatório de Clientes"));
        
        JTextArea clientesInfoArea = new JTextArea();
        clientesInfoArea.setEditable(false);
        clientesInfoArea.setText("Relatório detalhado de clientes e suas compras.");
        clientesInfoArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JButton gerarRelClientesBtn = new JButton("Gerar Relatório de Clientes");
        
        relClientesPanel.add(clientesInfoArea, BorderLayout.CENTER);
        relClientesPanel.add(gerarRelClientesBtn, BorderLayout.SOUTH);
        
        // Relatório de vendas por produto
        JPanel relVendasProdutoPanel = new JPanel(new BorderLayout());
        relVendasProdutoPanel.setBorder(BorderFactory.createTitledBorder("Vendas por Produto"));
        
        JTextArea vendasProdutoInfoArea = new JTextArea();
        vendasProdutoInfoArea.setEditable(false);
        vendasProdutoInfoArea.setText("Estatísticas de vendas por produto ou serviço.");
        vendasProdutoInfoArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JButton gerarRelVendasProdutoBtn = new JButton("Gerar Relatório de Vendas por Produto");
        
        relVendasProdutoPanel.add(vendasProdutoInfoArea, BorderLayout.CENTER);
        relVendasProdutoPanel.add(gerarRelVendasProdutoBtn, BorderLayout.SOUTH);
        
        opcoesPanel.add(relVendasPanel);
        opcoesPanel.add(relEstoquePanel);
        opcoesPanel.add(relClientesPanel);
        opcoesPanel.add(relVendasProdutoPanel);
        
        panel.add(opcoesPanel, BorderLayout.CENTER);
        
        // Ações dos botões
        gerarRelVendasBtn.addActionListener(e -> {
            // Janela para definir período
            JDialog periodoDialog = new JDialog();
            periodoDialog.setTitle("Selecionar Período");
            periodoDialog.setSize(350, 200);
            periodoDialog.setLocationRelativeTo(panel);
            periodoDialog.setModal(true);
            
            JPanel periodoPanel = new JPanel(new GridLayout(0, 2, 10, 10));
            periodoPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
            
            periodoPanel.add(new JLabel("Data Inicial:"));
            JFormattedTextField dataInicialField = new JFormattedTextField();
            try {
                dataInicialField.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(
                        new javax.swing.text.MaskFormatter("##/##/####")));
            } catch (ParseException ex) {
                ex.printStackTrace();
            }
            periodoPanel.add(dataInicialField);
            
            periodoPanel.add(new JLabel("Data Final:"));
            JFormattedTextField dataFinalField = new JFormattedTextField();
            try {
                dataFinalField.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(
                        new javax.swing.text.MaskFormatter("##/##/####")));
            } catch (ParseException ex) {
                ex.printStackTrace();
            }
            periodoPanel.add(dataFinalField);
            
            JButton gerarBtn = new JButton("Gerar");
            JButton cancelarBtn = new JButton("Cancelar");
            
            JPanel botoesPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            botoesPanel.add(gerarBtn);
            botoesPanel.add(cancelarBtn);
            
            periodoPanel.add(new JLabel(""));
            periodoPanel.add(botoesPanel);
            
            gerarBtn.addActionListener(evt -> {
                // Implementação real geraria um relatório com base nas datas
                JOptionPane.showMessageDialog(periodoDialog, 
                    "Funcionalidade de geração de relatórios não implementada nesta versão.");
                periodoDialog.dispose();
            });
            
            cancelarBtn.addActionListener(evt -> periodoDialog.dispose());
            
            periodoDialog.add(periodoPanel);
            periodoDialog.setVisible(true);
        });
        
        gerarRelEstoqueBtn.addActionListener(e -> {
            JOptionPane.showMessageDialog(panel, 
                "Funcionalidade de geração de relatórios não implementada nesta versão.");
        });
        
        gerarRelClientesBtn.addActionListener(e -> {
            JOptionPane.showMessageDialog(panel, 
                "Funcionalidade de geração de relatórios não implementada nesta versão.");
        });
        
        gerarRelVendasProdutoBtn.addActionListener(e -> {
            JOptionPane.showMessageDialog(panel, 
                "Funcionalidade de geração de relatórios não implementada nesta versão.");
        });
        
        return panel;
    }
    
    private JPanel criarPainelDefinicoes() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel titulo = new JLabel("Definições");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titulo.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        panel.add(titulo, BorderLayout.NORTH);
        
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // Campos do formulário
        formPanel.add(new JLabel("Nome da Empresa:"), gbc);
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;
        JTextField empresaField = new JTextField(definicoes.getNomeDaEmpresa(), 30);
        formPanel.add(empresaField, gbc);
        
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE;
        formPanel.add(new JLabel("Nome de Utilizador:"), gbc);
        
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        JTextField usuarioField = new JTextField(definicoes.getUserName(), 20);
        formPanel.add(usuarioField, gbc);
        
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.fill = GridBagConstraints.NONE;
        formPanel.add(new JLabel("Senha:"), gbc);
        
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        JTextField senhaField = new JTextField(definicoes.getSenha(), 20);
        formPanel.add(senhaField, gbc);
        
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.fill = GridBagConstraints.NONE;
        formPanel.add(new JLabel("Taxa de IVA (%):"), gbc);
        
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        JSpinner ivaSpinner = new JSpinner(new SpinnerNumberModel(
            definicoes.getIva(), 0, 100, 0.5));
        formPanel.add(ivaSpinner, gbc);
        
        // Tema
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.anchor = GridBagConstraints.EAST;
        formPanel.add(new JLabel("Tema:"), gbc);
        
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        
        ButtonGroup temaGroup = new ButtonGroup();
        
        JRadioButton claroRadio = new JRadioButton("Claro");
        claroRadio.setSelected(!definicoes.isDarkMode());
        
        JRadioButton escuroRadio = new JRadioButton("Escuro");
        escuroRadio.setSelected(definicoes.isDarkMode());
        
        temaGroup.add(claroRadio);
        temaGroup.add(escuroRadio);
        
        JPanel temaPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        temaPanel.add(claroRadio);
        temaPanel.add(escuroRadio);
        
        formPanel.add(temaPanel, gbc);
        
        // Botões
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        
        JPanel botoesPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton cancelarBtn = new JButton("Cancelar");
        JButton salvarBtn = new JButton("Salvar");
        
        cancelarBtn.addActionListener(e -> {
            // Reverter para valores originais
            empresaField.setText(definicoes.getNomeDaEmpresa());
            usuarioField.setText(definicoes.getUserName());
            senhaField.setText(definicoes.getSenha());
            ivaSpinner.setValue(definicoes.getIva());
            claroRadio.setSelected(!definicoes.isDarkMode());
            escuroRadio.setSelected(definicoes.isDarkMode());
        });
        
        salvarBtn.addActionListener(e -> {
            // Validar campos
            if (empresaField.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(panel, "O nome da empresa não pode ficar vazio.");
                return;
            }
            
            if (usuarioField.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(panel, "O nome de utilizador não pode ficar vazio.");
                return;
            }
            
            if (senhaField.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(panel, "A senha não pode ficar vazia.");
                return;
            }
            
            // Atualizar definições
            definicoes.setNomeDaEmpresa(empresaField.getText().trim());
            definicoes.setUserName(usuarioField.getText().trim());
            definicoes.setSenha(senhaField.getText().trim());
            definicoes.setIva((Double)ivaSpinner.getValue());
            definicoes.setDarkMode(escuroRadio.isSelected());
            
            // Salvar no DAO
            definicoesDao.delete(definicoes.getNomeDaEmpresa(), d -> d.getNomeDaEmpresa());
            boolean inserido = definicoesDao.insert(definicoes);
            
            if (inserido) {
                JOptionPane.showMessageDialog(panel, "Definições salvas com sucesso!");
                
                // Atualizar label da empresa no header
                empresaLabel.setText(definicoes.getNomeDaEmpresa());
                
                // Atualizar tema se necessário
                if (definicoes.isDarkMode() != isDarkMode) {
                    mudarTema(definicoes.isDarkMode());
                }
            } else {
                JOptionPane.showMessageDialog(panel, "Erro ao salvar definições.");
            }
        });
        
        botoesPanel.add(cancelarBtn);
        botoesPanel.add(salvarBtn);
        
        formPanel.add(botoesPanel, gbc);
        
        panel.add(formPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    public static void main(String[] args) {
        try {
            // Configuração inicial do tema
            UIManager.setLookAndFeel(new FlatLightLaf());
        } catch (Exception ex) {
            System.err.println("Failed to initialize LaF: " + ex);
        }
        
        SwingUtilities.invokeLater(() -> {
            new DashboardBombasOK();
        });
    }
}