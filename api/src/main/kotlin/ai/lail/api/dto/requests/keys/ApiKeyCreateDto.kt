package ai.lail.api.dto.requests.keys

import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import org.hibernate.validator.constraints.Length
import java.io.Serializable

class ApiKeyCreateDto : Serializable {

    @NotNull
    @Length(min = 1, max = 64)
    var name: String = ""

    @NotNull
    @Size(min = 1, max = 50)
    var permissions: List<String> = listOf()

}