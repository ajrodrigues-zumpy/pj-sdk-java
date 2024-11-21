package inter.banking.webhook;

import inter.banking.model.FiltroBuscarCallbacks;
import inter.banking.model.PaginaCallbacks;
import inter.banking.model.RespostaBuscarCallbacks;
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
import static inter.constants.Constants.GENERIC_EXCEPTION_MESSAGE;
import static inter.constants.Constants.URL_BANKING_WEBHOOK;
import static inter.constants.Constants.ESCOPO_BANKING_WEBHOOK_BANKING_READ;

@Slf4j
public class ConsultarCallbacks {

    public PaginaCallbacks recuperar(Config config, String tipoWebhook, String dataHoraInicio, String dataHoraFim, int pagina, Integer tamanhoPagina, FiltroBuscarCallbacks filtro) throws SdkException {
        log.info("ConsultarCallbacks {} {}-{}", config.getClientId(), dataHoraInicio, dataHoraFim);
        return getPage(config, tipoWebhook, dataHoraInicio, dataHoraFim, pagina, tamanhoPagina, filtro);
    }

    public List<RespostaBuscarCallbacks> recuperar(Config config, String tipoWebhook, String dataInicial, String dataFinal, FiltroBuscarCallbacks filtro) throws SdkException {
        log.info("ConsultarCallbacks {} {}-{}", config.getClientId(), dataInicial, dataFinal);
        int pagina = 0;
        PaginaCallbacks paginaCallbacks;
        List<RespostaBuscarCallbacks> callbacks = new ArrayList<>();
        do {
            paginaCallbacks = getPage(config, tipoWebhook, dataInicial, dataFinal, pagina, null, filtro);
            callbacks.addAll(paginaCallbacks.getData());
            pagina++;
        } while (pagina < paginaCallbacks.getTotalPaginas());

        return callbacks;
    }

    private PaginaCallbacks getPage(Config config, String tipoWebhook, String dataHoraInicio, String dataHoraFim, int pagina, Integer tamanhoPagina, FiltroBuscarCallbacks filtro) throws SdkException {
        String url1 = URL_BANKING_WEBHOOK.replace("AMBIENTE", config.getAmbiente());
        String url2 = url1 + "/" + tipoWebhook + "/callbacks";
        String url = url2
                + "?dataHoraInicio=" + dataHoraInicio
                + "&dataHoraFim=" + dataHoraFim
                + "&pagina=" + pagina
                + (tamanhoPagina != null ? "&tamanhoPagina=" + tamanhoPagina : "")
                + addfilters(filtro);
        String json = HttpUtils.callGet(config, url, ESCOPO_BANKING_WEBHOOK_BANKING_READ, "Erro ao recuperar callbacks");
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

        if (filtro.getCodigoTransacao() != null) {
            filter.append("&codigoTransacao").append("=").append(filtro.getCodigoTransacao());
        }

        if (filtro.getEndToEnd() != null) {
            filter.append("&endToEnd").append("=").append(filtro.getEndToEnd());
        }

        return filter.toString();
    }

}