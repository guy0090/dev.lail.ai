package ai.lail.api.dto.requests.users

import jakarta.validation.constraints.AssertTrue
import jakarta.validation.constraints.Pattern

class UserProfileViewDto {
    @Pattern(regexp = "^[a-zA-Z0-9]{24}$", message = "User ID must be a valid Object ID")
    var id: String? = null

    @Pattern(
        regexp = "^[a-zA-Z0-9_]{2,20}$",
        message = "Custom URL slug must be alphanumeric with underscores and between 2 and 20 characters long."
    )
    var slug: String? = null

    /**
     * Validate that either the ID or the slug is provided, but not both.
     */
    @AssertTrue(message = "You must provide either a user ID or a custom URL slug, but not both.")
    fun validate(): Boolean {
        return (id.isNullOrBlank() && !slug.isNullOrBlank()) || (!id.isNullOrBlank() && slug.isNullOrBlank())
    }
}