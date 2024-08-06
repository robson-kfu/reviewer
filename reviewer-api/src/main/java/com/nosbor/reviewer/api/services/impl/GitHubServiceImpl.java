package com.nosbor.reviewer.api.services.impl;

import com.nosbor.reviewer.api.clients.github.models.GitHubTokenTO;
import com.nosbor.reviewer.api.helpers.ValidationHelper;
import com.nosbor.reviewer.api.models.RequestRevisionTO;
import com.nosbor.reviewer.api.services.IVSCService;
import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;
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
import java.util.Date;
import java.util.Map;
import java.util.Objects;

import static com.nosbor.reviewer.api.models.VCSAvailableServicesEnum.GITHUB;

@Service
@Slf4j
public class GitHubServiceImpl implements IVSCService {

    private static final long EXPIRATION_TIME = 600_000; // 10 minutes
    public static final String BEARER_S = "Bearer %s";
    public static final String VCS_SERVICES_GITHUB_INSTALLATION_ID = "vcs.services.github.installationId";
    public static final String VCS_SERVICES_GITHUB_BASE_URL = "vcs.services.github.baseUrl";
    public static final String VCS_SERVICES_GITHUB_API_BASE_URL = "vcs.services.github.apiBaseUrl";
    public static final String VCS_SERVICES_GITHUB_API_VERSION = "vcs.services.github.apiVersion";
    public static final String VCS_SERVICES_GITHUB_APP_ID = "vcs.services.github.appId";
    public static final String VCS_SERVICES_GITHUB_PEM_PATH = "vcs.services.github.pemPath";

    private GitHubTokenTO gitHubTokenTO;

    private final String installationId;
    private final String appId;
    private final String pemPath;
    private final String baseUrl;
    private final String apiBaseUrl;
    private final String apiVersion;

    private final WebClient baseClient;
    private final WebClient apiClient;

    public GitHubServiceImpl(@Value("${" + VCS_SERVICES_GITHUB_INSTALLATION_ID + ":}") String installationId,
                             @Value("${" + VCS_SERVICES_GITHUB_APP_ID + ":}") String appId,
                             @Value("${" + VCS_SERVICES_GITHUB_PEM_PATH + ":}") String pemPath,
                             @Value("${" + VCS_SERVICES_GITHUB_BASE_URL + ":}") String baseUrl,
                             @Value("${" + VCS_SERVICES_GITHUB_API_BASE_URL + ":}") String apiBaseUrl,
                             @Value("${" + VCS_SERVICES_GITHUB_API_VERSION + ":}") String apiVersion) {
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
        log.info("Diff encontrado. Tamanho do diff {}", diff.length());
        return diff;
    }

    @Override
    public void validate() {
        log.info("Validando as configurações do git hub");
        ValidationHelper.validate(Map.of(
                this.installationId, VCS_SERVICES_GITHUB_INSTALLATION_ID,
                this.baseUrl, VCS_SERVICES_GITHUB_BASE_URL,
                this.apiBaseUrl, VCS_SERVICES_GITHUB_API_BASE_URL,
                this.apiVersion, VCS_SERVICES_GITHUB_API_VERSION,
                this.appId, VCS_SERVICES_GITHUB_APP_ID,
                this.pemPath, VCS_SERVICES_GITHUB_PEM_PATH
        ), GITHUB.toString());
        log.info("Configurações do git hub estão OK!");
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
