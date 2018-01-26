package ru.wildbot.bootstrap.core;

import com.google.gson.annotations.SerializedName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import ru.wildbot.core.data.json.AbstractJsonData;

@Data
@EqualsAndHashCode(callSuper = true)
public class CoreSettings extends AbstractJsonData {
    @SerializedName("name") private String name = "Core-#1";
    @SerializedName("folder") private String folder = "Core-1";
    @SerializedName("log-size") private int logSize = 20;
    @SerializedName("command-arguments") private String[] commandArguments = {};
}
