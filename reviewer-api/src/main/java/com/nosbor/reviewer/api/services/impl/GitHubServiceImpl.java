package com.nosbor.reviewer.api.services.impl;

import com.nosbor.reviewer.api.clients.github.models.GitHubTokenTO;
import com.nosbor.reviewer.api.models.RequestRevisionTO;
import com.nosbor.reviewer.api.services.IVSCService;
import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Service
@Slf4j
public class GitHubServiceImpl implements IVSCService {

    private static final long EXPIRATION_TIME = 600_000; // 10 minutes
    public static final String A_PROPRIEDADE_S_DEVE_SER_INFORMADA_AO_USAR_O_GITHUB = "A propriedade %s deve ser informada ao usar o GITHUB!";
    public static final String BEARER_S = "Bearer %s";

    private GitHubTokenTO gitHubTokenTO;

    private final String installationId;
    private final String appId;
    private final String pemPath;
    private final String baseUrl;
    private final String apiBaseUrl;
    private final String apiVersion;

    private final WebClient baseClient;
    private final WebClient apiClient;

    public GitHubServiceImpl(@Value("${vcs.services.github.installationId:}") String installationId,
                             @Value("${vcs.services.github.appId:}") String appId,
                             @Value("${vcs.services.github.pemPath:}") String pemPath,
                             @Value("${vcs.services.github.baseUrl:}") String baseUrl,
                             @Value("${vcs.services.github.apiBaseUrl:}") String apiBaseUrl,
                             @Value("${vcs.services.github.apiVersion:}") String apiVersion) {
        this.installationId = installationId;
        this.appId = appId;
        this.pemPath = pemPath;
        this.baseUrl = baseUrl;
        this.apiBaseUrl = apiBaseUrl;
        this.apiVersion = apiVersion;
        this.baseClient = WebClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader("Accept", "text/html")
                .build();
        this.apiClient = WebClient.builder()
                .baseUrl(apiBaseUrl)
                .defaultHeader("Accept", "application/vnd.github+json")
                .defaultHeader("X-GitHub-Api-Version", apiVersion)
                .build();
    }

    @Override
    public String getPullRequestDiff(RequestRevisionTO requestRevisionTO) throws Exception {
        log.info("Buscando informações do diff da PR");
        generateJWT();
        byte[] rawResponse = baseClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/raw/{owner}/{repo}/pull/{pull_number}.diff")
                        .build(requestRevisionTO.getOwner(),
                                requestRevisionTO.getRepoName(),
                                requestRevisionTO.getPullRequestId()))
                .header("Authorization", String.format(BEARER_S, this.gitHubTokenTO.getToken()))
                .retrieve()
                .bodyToMono(byte[].class)
                .block();
        Objects.requireNonNull(rawResponse);
        String diff = new String(rawResponse);
        log.info("Diff encontrado: {}", diff);
        return diff;
    }

    @Override
    public String getPullRequestContext(RequestRevisionTO requestRevisionTO) {
        return "implementar";
    }

    @Override
    public void validate() {
        List<String> errorsMsg = new ArrayList<>();

        validateProperty(this.installationId, "vcs.services.github.installationId", errorsMsg);
        validateProperty(this.baseUrl, "vcs.services.github.baseUrl", errorsMsg);
        validateProperty(this.apiBaseUrl, "vcs.services.github.apiBaseUrl", errorsMsg);
        validateProperty(this.apiVersion, "vcs.services.github.apiVersion", errorsMsg);
        validateProperty(this.appId, "vcs.services.github.appId", errorsMsg);
        validateProperty(this.pemPath, "vcs.services.github.pemPath", errorsMsg);

        if (!errorsMsg.isEmpty()) {
            throw new RuntimeException(errorsMsg.toString());
        }
    }

    private void validateProperty(String property, String propertyName, List<String> errorsMsg) {
        if (Strings.isBlank(property)) {
            errorsMsg.add(String.format(A_PROPRIEDADE_S_DEVE_SER_INFORMADA_AO_USAR_O_GITHUB, propertyName));
        }
    }

    private static PrivateKey getPrivateKey(String filename) throws Exception {
        PemReader pemReader = new PemReader(Files.newBufferedReader(Paths.get(filename)));
        PemObject pemObject = pemReader.readPemObject();
        byte[] content = pemObject.getContent();
        pemReader.close();

        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(content);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        return kf.generatePrivate(keySpec);
    }

    private void generateJWT() throws Exception {
        log.info("Gerando token...");
        if (this.gitHubTokenTO == null ||
                this.gitHubTokenTO.getExpiresAt().isBefore(Instant.now())) {
            log.info("Token é nulo ou expirado, renovando...");
            long now = System.currentTimeMillis();
            String internalToken = Jwts.builder()
                    .issuer(String.valueOf(appId))
                    .issuedAt(new Date(now))
                    .expiration(new Date(now + EXPIRATION_TIME))
                    .signWith(getPrivateKey(pemPath))
                    .compact();
            this.gitHubTokenTO = apiClient.post()
                    .uri(uriBuilder -> uriBuilder
                            .path("/app/installations/{installation_id}/access_tokens")
                            .build(installationId))
                    .header("Authorization", String.format(BEARER_S, internalToken))
                    .retrieve()
                    .bodyToMono(GitHubTokenTO.class)
                    .block();
        }
        log.info("Token gerado {}", this.gitHubTokenTO);
    }
}
