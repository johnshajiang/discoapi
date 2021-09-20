/*
 * Copyright (c) 2021.
 *
 * This file is part of DiscoAPI.
 *
 *     DiscoAPI is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 2 of the License, or
 *     (at your option) any later version.
 *
 *     DiscoAPI is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with DiscoAPI.  If not, see <http://www.gnu.org/licenses/>.
 */

package io.foojay.api.distribution;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.foojay.api.CacheManager;
import io.foojay.api.pkg.Architecture;
import io.foojay.api.pkg.ArchiveType;
import io.foojay.api.pkg.Bitness;
import io.foojay.api.pkg.Distro;
import io.foojay.api.pkg.HashAlgorithm;
import io.foojay.api.pkg.MajorVersion;
import io.foojay.api.pkg.OperatingSystem;
import io.foojay.api.pkg.PackageType;
import io.foojay.api.pkg.Pkg;
import io.foojay.api.pkg.ReleaseStatus;
import io.foojay.api.pkg.SemVer;
import io.foojay.api.pkg.SignatureType;
import io.foojay.api.pkg.TermOfSupport;
import io.foojay.api.pkg.VersionNumber;
import io.foojay.api.util.Constants;
import io.foojay.api.util.GithubTokenPool;
import io.foojay.api.util.Helper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.TreeSet;
import java.util.concurrent.CompletionException;
import java.util.stream.Collectors;

import static io.foojay.api.pkg.ReleaseStatus.EA;
import static io.foojay.api.pkg.ReleaseStatus.GA;


public class Temurin implements Distribution {
    private static final Logger LOGGER = LoggerFactory.getLogger(Temurin.class);

    private static final String        PACKAGE_URL            = "https://api.github.com/repos/adoptium/";

    // URL parameters
    private static final String        ARCHITECTURE_PARAM     = "architecture";
    private static final String        OPERATING_SYSTEM_PARAM = "os";
    private static final String        ARCHIVE_TYPE_PARAM     = "";
    private static final String        PACKAGE_TYPE_PARAM     = "image_type";
    private static final String        RELEASE_STATUS_PARAM   = "release_type";
    private static final String        SUPPORT_TERM_PARAM     = "";
    private static final String        BITNESS_PARAM          = "";

    private static final HashAlgorithm HASH_ALGORITHM         = HashAlgorithm.NONE;
    private static final String        HASH_URI               = "";
    private static final SignatureType SIGNATURE_TYPE         = SignatureType.NONE;
    private static final HashAlgorithm SIGNATURE_ALGORITHM    = HashAlgorithm.NONE;
    private static final String        SIGNATURE_URI          = "";
    private static final String        OFFICIAL_URI           = "https://adoptium.net/";


    @Override public Distro getDistro() { return Distro.TEMURIN; }

    @Override public String getName() { return getDistro().getUiString(); }

    @Override public String getPkgUrl() { return PACKAGE_URL; }

    @Override public String getArchitectureParam() { return ARCHITECTURE_PARAM; }

    @Override public String getOperatingSystemParam() { return OPERATING_SYSTEM_PARAM; }

    @Override public String getArchiveTypeParam() { return ARCHIVE_TYPE_PARAM; }

    @Override public String getPackageTypeParam() { return PACKAGE_TYPE_PARAM; }

    @Override public String getReleaseStatusParam() { return RELEASE_STATUS_PARAM; }

    @Override public String getTermOfSupportParam() { return SUPPORT_TERM_PARAM; }

    @Override public String getBitnessParam() { return BITNESS_PARAM; }

    @Override public HashAlgorithm getHashAlgorithm() { return HASH_ALGORITHM; }

    @Override public String getHashUri() { return HASH_URI; }

    @Override public SignatureType getSignatureType() { return SIGNATURE_TYPE; }

    @Override public HashAlgorithm getSignatureAlgorithm() { return SIGNATURE_ALGORITHM; }

    @Override public String getSignatureUri() { return SIGNATURE_URI; }

    @Override public String getOfficialUri() { return OFFICIAL_URI; }

    @Override public List<String> getSynonyms() {
        return List.of("temurin", "Temurin", "TEMURIN");
    }

    @Override public List<SemVer> getVersions() {
        return CacheManager.INSTANCE.pkgCache.getPkgs()
                                             .stream()
                                             .filter(pkg -> Distro.TEMURIN.get().equals(pkg.getDistribution()))
                                             .map(pkg -> pkg.getSemver())
                                             .collect(Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(SemVer::toString))))
                                             .stream()
                                             .sorted(Comparator.comparing(SemVer::getVersionNumber).reversed())
                                             .collect(Collectors.toList());
    }


    @Override public String getUrlForAvailablePkgs(final VersionNumber versionNumber,
                                                   final boolean latest, final OperatingSystem operatingSystem,
                                                   final Architecture architecture, final Bitness bitness, final ArchiveType archiveType, final PackageType packageType,
                                                   final Boolean javafxBundled, final ReleaseStatus releaseStatus, final TermOfSupport termOfSupport) {
        StringBuilder queryBuilder = new StringBuilder();

        queryBuilder.append(PACKAGE_URL);

        LOGGER.debug("Query string for {}: {}", this.getName(), queryBuilder.toString());

        return queryBuilder.toString();
    }

    @Override public List<Pkg> getPkgFromJson(final JsonObject jsonObj, final VersionNumber versionNumber, final boolean latest, final OperatingSystem operatingSystem,
                                              final Architecture architecture, final Bitness bitness, final ArchiveType archiveType, final PackageType packageType,
                                              final Boolean javafxBundled, final ReleaseStatus releaseStatus, final TermOfSupport termOfSupport) {
        List<Pkg> pkgs = new ArrayList<>();

        return pkgs;
    }

    public List<Pkg> getAllPkgs() {
        List<Pkg> pkgs = new ArrayList<>();
        try {
            for (int i = 8 ; i <= MajorVersion.getLatest(true).getAsInt() ; i++) {
                String packageUrl = PACKAGE_URL + "temurin" + i + "-binaries/releases";
                // Get all packages from github
                try {
                    HttpResponse<String> response = Helper.get(packageUrl, Map.of("accept", "application/vnd.github.v3+json",
                                                                                  "authorization", GithubTokenPool.INSTANCE.next()));
                    if (response.statusCode() == 200) {
                        String      bodyText = response.body();
                        Gson        gson     = new Gson();
                        JsonElement element  = gson.fromJson(bodyText, JsonElement.class);
                        if (element instanceof JsonArray) {
                            JsonArray jsonArray = element.getAsJsonArray();
                            pkgs.addAll(getAllPkgsFromJson(jsonArray, i));
                        }
            } else {
                        // Problem with url request
                        LOGGER.debug("Response ({}) {} ", response.statusCode(), response.body());
                    }
                } catch (CompletionException e) {
                    LOGGER.error("Error fetching packages for distribution {} from {}", getName(), packageUrl);
                }
            }
        } catch (Exception e) {
            LOGGER.error("Error fetching all packages from Temurin. {}", e);
        }
        return pkgs;
    }

    public List<Pkg> getAllPkgsFromJson(final JsonArray jsonArray, final int featureVersion) {
        List<Pkg> pkgs = new ArrayList<>();
        Optional<MajorVersion> majorVersionOpt = CacheManager.INSTANCE.getMajorVersions().stream().filter(majorVersion -> majorVersion.getAsInt() == featureVersion).findFirst();
        if (majorVersionOpt.isPresent()) {
            boolean isEarlyAccessOnly = majorVersionOpt.get().isEarlyAccessOnly();
            LocalDateTime publishedAt     = LocalDateTime.MIN;
            LocalDateTime lastPublishedAt = publishedAt;
        for (int i = 0; i < jsonArray.size(); i++) {
            JsonObject jsonObj = jsonArray.get(i).getAsJsonObject();
            if (jsonObj.has("prerelease")) {
                boolean prerelease = jsonObj.get("prerelease").getAsBoolean();
                    if (prerelease && !isEarlyAccessOnly) { continue; }
                }
                if (jsonObj.has("published_at")) {
                    publishedAt = LocalDateTime.parse(jsonObj.get("published_at").getAsString(), DateTimeFormatter.ISO_DATE_TIME);
                }
                JsonArray assets = jsonObj.getAsJsonArray("assets");
                for (JsonElement element : assets) {
                    JsonObject assetJsonObj = element.getAsJsonObject();
                    String     filename     = assetJsonObj.get("name").getAsString();

                    if (null == filename || filename.isEmpty() || filename.endsWith("txt") || filename.contains("debugimage") || filename.contains("testimage") || filename.endsWith("json")) { continue; }
                    if (filename.contains("-debug-")) { continue; }
                    if (null == filename || !filename.startsWith("OpenJDK")) { continue; }

                    final String   withoutPrefix = filename.replaceAll("OpenJDK[0-9]+U?\\-", "");
                    final String   withoutSuffix = withoutPrefix.substring(0, withoutPrefix.lastIndexOf("."));
                    final String[] filenameParts = withoutSuffix.split("_");

                    final VersionNumber versionNumber;
                    final MajorVersion  majorVersion;
                    final ReleaseStatus releaseStatus;
                    if (isEarlyAccessOnly) {
                        versionNumber = majorVersionOpt.get().getVersionNumber();
                        majorVersion  = majorVersionOpt.get();
                        releaseStatus = EA;
                    } else {
                        versionNumber = VersionNumber.fromText(filenameParts[4] + (filenameParts.length == 6 ? ("+b" + filenameParts[5]) : ""));
                        majorVersion  = versionNumber.getMajorVersion();
                        releaseStatus = (filename.contains("-ea.") || majorVersion.equals(MajorVersion.getLatest(true))) ? EA : GA;
                    }
                    String downloadLink = assetJsonObj.get("browser_download_url").getAsString();

                    PackageType packageType = PackageType.fromText(filenameParts[0]);

                    OperatingSystem operatingSystem = Constants.OPERATING_SYSTEM_LOOKUP.entrySet()
                                                                                       .stream()
                                                                                       .filter(entry -> withoutSuffix.contains(entry.getKey()))
                                                                                       .findFirst()
                                                                                       .map(Entry::getValue)
                                                                                       .orElse(OperatingSystem.NOT_FOUND);
                    if (OperatingSystem.NOT_FOUND == operatingSystem) {
                            LOGGER.debug("Operating System not found in Temurin for filename: {}", filename);
                        continue;
                    }


                    final Architecture architecture = Constants.ARCHITECTURE_LOOKUP.entrySet()
                                                                                   .stream()
                                                                                   .filter(entry -> withoutSuffix.contains(entry.getKey()))
                                                                                   .findFirst()
                                                                                   .map(Entry::getValue)
                                                                                   .orElse(Architecture.NOT_FOUND);
                    if (Architecture.NOT_FOUND == architecture) {
                            LOGGER.debug("Architecture not found in Temurin for filename: {}", filename);
                        continue;
                    }

                    final ArchiveType archiveType = Helper.getFileEnding(filename);
                    if (OperatingSystem.MACOS == operatingSystem) {
                        switch (archiveType) {
                        case DEB:
                            case RPM:
                                operatingSystem = OperatingSystem.LINUX;
                                break;
                        case CAB:
                        case MSI:
                            case EXE:
                                operatingSystem = OperatingSystem.WINDOWS;
                                break;
                        }
                    }

                    Pkg pkg = new Pkg();
                    pkg.setDistribution(Distro.TEMURIN.get());
                    pkg.setArchitecture(architecture);
                    pkg.setBitness(architecture.getBitness());
                    pkg.setVersionNumber(versionNumber);
                    pkg.setJavaVersion(versionNumber);
                    pkg.setDistributionVersion(versionNumber);
                    pkg.setDirectDownloadUri(downloadLink);
                    pkg.setFileName(filename);
                    pkg.setArchiveType(archiveType);
                    pkg.setJavaFXBundled(false);
                    pkg.setTermOfSupport(majorVersion.getTermOfSupport());
                    pkg.setReleaseStatus(releaseStatus);
                    pkg.setPackageType(packageType);
                    pkg.setOperatingSystem(operatingSystem);
                    pkg.setFreeUseInProduction(Boolean.TRUE);

                    if (isEarlyAccessOnly) {
                        if (publishedAt.isAfter(lastPublishedAt)) { pkgs.add(pkg); }
                    } else {
                        pkgs.add(pkg);
                    }
                }
                lastPublishedAt = publishedAt;
            }
        }
        LOGGER.debug("Successfully fetched {} packages from {}", pkgs.size(), PACKAGE_URL);
        return pkgs;
    }
}
