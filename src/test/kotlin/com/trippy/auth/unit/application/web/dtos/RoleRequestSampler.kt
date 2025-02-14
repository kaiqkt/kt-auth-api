package com.trippy.auth.unit.application.web.dtos

import com.trippy.auth.generated.application.web.dtos.DeleteRolesRequestV1
import com.trippy.auth.generated.application.web.dtos.RoleRequestV1
import com.trippy.auth.generated.application.web.dtos.UpdateRoleRequestV1

object RoleRequestSampler {
    fun sample() = RoleRequestV1(
        name = "Admin",
        description = "Admin role",
    )

    fun sampleUpdate() = UpdateRoleRequestV1(
        description = "new description",
    )

    fun sampleDelete() = DeleteRolesRequestV1(
        ids = listOf("1", "2"),
    )
}