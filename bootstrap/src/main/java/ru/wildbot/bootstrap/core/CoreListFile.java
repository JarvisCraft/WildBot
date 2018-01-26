package ru.wildbot.bootstrap.core;

import com.google.gson.annotations.SerializedName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import ru.wildbot.core.data.json.AbstractJsonData;

@Data
@EqualsAndHashCode(callSuper = true)
public class CoreListFile extends AbstractJsonData {
    @SerializedName("cores") private CoreSettings[] cores = {new CoreSettings()};
}
