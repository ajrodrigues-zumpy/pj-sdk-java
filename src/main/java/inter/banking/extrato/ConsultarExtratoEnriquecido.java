package inter.banking.extrato;

import inter.banking.model.PaginaExtratoEnriquecido;
import inter.banking.model.FiltroConsultarExtratoEnriquecido;
import inter.banking.model.TransacaoEnriquecida;
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
import static inter.constants.Constants.ESCOPO_EXTRATO_READ;
import static inter.constants.Constants.GENERIC_EXCEPTION_MESSAGE;
import static inter.constants.Constants.URL_BANKING_EXTRATO_ENRIQUECIDO;

@Slf4j
public class ConsultarExtratoEnriquecido {

    public PaginaExtratoEnriquecido consultar(Config config, String dataInicial, String dataFinal, int pagina, Integer tamanhoPagina, FiltroConsultarExtratoEnriquecido filtro) throws SdkException {
        log.info("ConsultarExtratoEnriquecido {} {}-{}", config.getClientId(), dataInicial, dataFinal);
        return getPage(config, dataInicial, dataFinal, pagina, tamanhoPagina, filtro);
    }

    public List<TransacaoEnriquecida> consultar(Config config, String dataInicial, String dataFinal, FiltroConsultarExtratoEnriquecido filtro) throws SdkException {
        log.info("ConsultarExtratoEnriquecido {} {}-{}", config.getClientId(), dataInicial, dataFinal);
        int pagina = 0;
        PaginaExtratoEnriquecido paginaTransacoes;
        List<TransacaoEnriquecida> transacoes = new ArrayList<>();
        do {
            paginaTransacoes = getPage(config, dataInicial, dataFinal, pagina, null, filtro);
            transacoes.addAll(paginaTransacoes.getTransacoes());
            pagina++;
        } while (pagina < paginaTransacoes.getTotalPaginas());
        return transacoes;
    }

    private PaginaExtratoEnriquecido getPage(Config config, String dataInicial, String dataFinal, int pagina, Integer tamanhoPagina, FiltroConsultarExtratoEnriquecido filtro) throws SdkException {
        String url = URL_BANKING_EXTRATO_ENRIQUECIDO.replace("AMBIENTE", config.getAmbiente()) + "?dataInicio=" + dataInicial + "&dataFim=" + dataFinal
                + "&pagina=" + pagina
                + (tamanhoPagina != null ? "&tamanhoPagina=" + tamanhoPagina : "")
                + addfilters(filtro);
        String json = HttpUtils.callGet(config, url, ESCOPO_EXTRATO_READ, "Erro ao consultar extrato enriquecido");
        try {
            return JsonUtils.read(json, PaginaExtratoEnriquecido.class);
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

    private String addfilters(FiltroConsultarExtratoEnriquecido filtro) {
        if (filtro == null) {
            return "";
        }
        StringBuilder filter = new StringBuilder();
        if (filtro.getTipoOperacao() != null) {
            filter.append("&tipoOperacao").append("=").append(filtro.getTipoOperacao().toString());
        }
        if (filtro.getTipoTransacao() != null) {
            filter.append("&tipoTransacao").append("=").append(filtro.getTipoTransacao().toString());
        }
        return filter.toString();
    }

}