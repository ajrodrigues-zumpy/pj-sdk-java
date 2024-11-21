package inter.cobrancav3.cobranca;

import inter.cobrancav3.model.FiltroRecuperarCobrancas;
import inter.cobrancav3.model.Ordenacao;
import inter.cobrancav3.model.PaginaCobrancas;
import inter.cobrancav3.model.CobrancaRecuperada;
import inter.exceptions.SdkException;
import inter.model.Config;
import inter.model.Erro;
import inter.utils.HttpUtils;
import inter.utils.JsonUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static inter.constants.Constants.CERTIFICATE_EXCEPTION_MESSAGE;
import static inter.constants.Constants.ESCOPO_BOLETO_COBRANCA_READ;
import static inter.constants.Constants.GENERIC_EXCEPTION_MESSAGE;
import static inter.constants.Constants.URL_COBRANCAS;

@Slf4j
public class RecuperaColecaoCobrancas {

    public PaginaCobrancas recuperar(Config config, String dataInicial, String dataFinal, int pagina, Integer tamanhoPagina, FiltroRecuperarCobrancas filtro, Ordenacao ordenacao) throws SdkException {
        log.info("RecuperarColecaoCobrancas {} {}-{}", config.getClientId(), dataInicial, dataFinal);
        return getPage(config, dataInicial, dataFinal, pagina, tamanhoPagina, filtro, ordenacao);
    }

    public List<CobrancaRecuperada> recuperar(Config config, String dataInicial, String dataFinal, FiltroRecuperarCobrancas filtro, Ordenacao ordenacao) throws SdkException {
        log.info("RecuperarColecaoCobrancas {} {}-{}", config.getClientId(), dataInicial, dataFinal);
        int pagina = 0;
        PaginaCobrancas paginaCobrancas;
        List<CobrancaRecuperada> cobrancas = new ArrayList<>();
        do {
            paginaCobrancas = getPage(config, dataInicial, dataFinal, pagina, null, filtro, ordenacao);
            cobrancas.addAll(paginaCobrancas.getCobrancas());
            pagina++;
        } while (pagina < paginaCobrancas.getTotalPaginas());
        return cobrancas;
    }

    private PaginaCobrancas getPage(Config config, String dataInicial, String dataFinal, int pagina, Integer tamanhoPagina, FiltroRecuperarCobrancas filtro, Ordenacao ordenacao) throws SdkException {
        String url = URL_COBRANCAS.replace("AMBIENTE", config.getAmbiente())
                + "?dataInicial=" + dataInicial
                + "&dataFinal=" + dataFinal
                + "&paginacao.paginaAtual=" + pagina
                + (tamanhoPagina != null ? "&paginacao.itensPorPagina=" + tamanhoPagina : "")
                + addfilters(filtro)
                + addSort(ordenacao);
        String json = HttpUtils.callGet(config, url, ESCOPO_BOLETO_COBRANCA_READ, "Erro ao recuperar cobranças");
        try {
            return JsonUtils.read(json, PaginaCobrancas.class);
        } catch (IOException ioException) {
            log.error(GENERIC_EXCEPTION_MESSAGE, ioException);
            throw new SdkException(
                    ioException.getMessage(),
                    Erro.builder()
                            .title(CERTIFICATE_EXCEPTION_MESSAGE).
                            detail(ioException.getMessage())
                            .build()
            );
        }
    }

    private String addfilters(FiltroRecuperarCobrancas filtro) {
        if (filtro == null) {
            return "";
        }

        StringBuilder filter = new StringBuilder();

        if (filtro.getFiltrarDataPor() != null) {
            filter.append("&filtrarDataPor").append("=").append(filtro.getFiltrarDataPor().toString());
        }

        if (filtro.getSituacao() != null) {
            filter.append("&situacao").append("=").append(filtro.getSituacao().toString());
        }

        if (filtro.getPessoaPagadora() != null) {
            filter.append("&pessoaPagadora").append("=").append(filtro.getPessoaPagadora());
        }

        if (filtro.getCpfCnpjPessoaPagadora() != null) {
            filter.append("&cpfCnpjPessoaPagadora").append("=").append(filtro.getCpfCnpjPessoaPagadora());
        }

        if (filtro.getSeuNumero() != null) {
            filter.append("&seuNumero").append("=").append(filtro.getSeuNumero());
        }

        if (filtro.getTipoCobranca() != null) {
            filter.append("&tipoCobranca").append("=").append(filtro.getTipoCobranca());
        }

        return filter.toString();
    }

    private String addSort(Ordenacao ordenacao) {
        if (ordenacao == null) {
            return "";
        }

        StringBuilder order = new StringBuilder();

        if (ordenacao.getOrdenarPor() != null) {
            order.append("&ordenarPor").append("=").append(ordenacao.getOrdenarPor().toString());
        }

        if (ordenacao.getTipoOrdenacao() != null) {
            order.append("&tipoOrdenacao").append("=").append(ordenacao.getTipoOrdenacao().toString());
        }

        return order.toString();
    }

}