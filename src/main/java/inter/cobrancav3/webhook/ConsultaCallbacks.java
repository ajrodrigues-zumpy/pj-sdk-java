package inter.cobrancav3.webhook;

import inter.cobrancav3.model.FiltroBuscarCallbacks;
import inter.cobrancav3.model.PaginaCallbacks;
import inter.cobrancav3.model.RespostaBuscarCallbacks;
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
import static inter.constants.Constants.URL_COBRANCAS_WEBHOOK_CALLBACKS;

@Slf4j
public class ConsultaCallbacks {

    public PaginaCallbacks recuperar(Config config, String dataHoraInicio, String dataHoraFim, int pagina, Integer tamanhoPagina, FiltroBuscarCallbacks filtro) throws SdkException {
        log.info("ConsultaCallbacks {} {}-{}", config.getClientId(), dataHoraInicio, dataHoraFim);
        return getPage(config, dataHoraInicio, dataHoraFim, pagina, tamanhoPagina, filtro);
    }

    public List<RespostaBuscarCallbacks> recuperar(Config config, String dataHoraInicio, String dataHoraFim, FiltroBuscarCallbacks filtro) throws SdkException {
        log.info("ConsultaCallbacks {} {}-{}", config.getClientId(), dataHoraInicio, dataHoraFim);
        int pagina = 0;
        PaginaCallbacks paginaCallbacks;
        List<RespostaBuscarCallbacks> callbacks = new ArrayList<>();
        do {
            paginaCallbacks = getPage(config, dataHoraInicio, dataHoraFim, pagina, null, filtro);
            callbacks.addAll(paginaCallbacks.getCallbacks());
            pagina++;
        } while (pagina < paginaCallbacks.getTotalPaginas());

        return callbacks;
    }

    private PaginaCallbacks getPage(Config config, String dataHoraInicio, String dataHoraFim, int pagina, Integer tamanhoPagina, FiltroBuscarCallbacks filtro) throws SdkException {
        String url = URL_COBRANCAS_WEBHOOK_CALLBACKS.replace("AMBIENTE", config.getAmbiente())
                + "?dataHoraInicio=" + dataHoraInicio
                + "&dataHoraFim=" + dataHoraFim
                + "&pagina=" + pagina
                + (tamanhoPagina != null ? "&itensPorPagina=" + tamanhoPagina : "")
                + addfilters(filtro);
        String json = HttpUtils.callGet(config, url, ESCOPO_BOLETO_COBRANCA_READ, "Erro ao recuperar callbacks");
        try {
            return JsonUtils.read(json, PaginaCallbacks.class);
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

    private String addfilters(FiltroBuscarCallbacks filtro) {
        if (filtro == null) {
            return "";
        }

        StringBuilder filter = new StringBuilder();

        if (filtro.getCodigoSolicitacao() != null) {
            filter.append("&codigoSolicitacao").append("=").append(filtro.getCodigoSolicitacao());
        }

        return filter.toString();
    }

}