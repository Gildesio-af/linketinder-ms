package zg.acelera.msnucleo.dto.candidate

import jakarta.validation.Valid
import jakarta.validation.constraints.NotNull
import zg.acelera.msnucleo.dto.user.UserCreateDTO

class CandidateCreateDTO {
    @Valid
    @NotNull(message = "User data cannot be null")
    UserCreateDTO user

    @Valid
    @NotNull(message = "Candidate data cannot be null")
    CandidateDataCreateDTO candidate
}
