package ru.wildbot.bootstrap.core;

import com.google.gson.annotations.SerializedName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import ru.wildbot.core.data.json.AbstractJsonData;

import java.util.Arrays;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class CoreManagerSettings extends AbstractJsonData {
    @SerializedName("file-name") private String fileName = "wildbot-core.jar";
    @SerializedName("path") private String path = "core/";
    @SerializedName("download-url") private String downloadUrl = "https://repository.sonatype.org/" +
            "service/local/artifact/maven/redirect?r=central-proxy&g=ru.wildbot&a=wildbot-core&v=LATEST";
    @SerializedName("start-command") private List<String> startCommand
            = Arrays.asList("java", "-jar", "wildbot-core.jar");
}
