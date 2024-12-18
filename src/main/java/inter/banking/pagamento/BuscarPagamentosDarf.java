package inter.banking.pagamento;

import com.fasterxml.jackson.core.type.TypeReference;
import inter.banking.model.FiltroBuscarPagamentosDarf;
import inter.banking.model.RetornoPagamentoDarf;
import inter.exceptions.SdkException;
import inter.model.Config;
import inter.model.Erro;
import inter.utils.HttpUtils;
import inter.utils.JsonUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.List;

import static inter.constants.Constants.CERTIFICATE_EXCEPTION_MESSAGE;
import static inter.constants.Constants.ESCOPO_PAGAMENTO_BOLETO_READ;
import static inter.constants.Constants.GENERIC_EXCEPTION_MESSAGE;
import static inter.constants.Constants.URL_BANKING_PAGAMENTO_DARF;

@Slf4j
public class BuscarPagamentosDarf {

    public List<RetornoPagamentoDarf> buscar(Config config, String dataInicial, String dataFinal, FiltroBuscarPagamentosDarf filtro) throws SdkException {
        log.info("BuscarPagamentosDarf {} {}-{}", config.getClientId(), dataInicial, dataFinal);
        String url = URL_BANKING_PAGAMENTO_DARF.replace("AMBIENTE", config.getAmbiente()) + "?dataInicio=" + dataInicial + "&dataFim=" + dataFinal
                + addfilters(filtro);
        String json = HttpUtils.callGet(config, url, ESCOPO_PAGAMENTO_BOLETO_READ, "Erro ao buscar pagamentos de DARF");
        try {
            return JsonUtils.read(json, new TypeReference<List<RetornoPagamentoDarf>>() {
            });
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

    private String addfilters(FiltroBuscarPagamentosDarf filtro) {
        if (filtro == null) {
            return "";
        }
        StringBuilder filter = new StringBuilder();
        if (filtro.getCodigoTransacao() != null) {
            filter.append("&codigoTransacao").append("=").append(filtro.getCodigoTransacao());
        }
        if (filtro.getCodigoReceita() != null) {
            filter.append("&codigoReceita").append("=").append(filtro.getCodigoReceita());
        }
        if (filtro.getFiltrarDataPor() != null) {
            filter.append("&filtrarDataPor").append("=").append(filtro.getFiltrarDataPor().toString());
        }
        return filter.toString();
    }

}