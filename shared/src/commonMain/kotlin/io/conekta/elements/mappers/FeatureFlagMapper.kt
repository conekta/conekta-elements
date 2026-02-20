package io.conekta.elements.mappers

import io.conekta.elements.models.FeatureFlag
import io.conekta.elements.dtos.FeatureFlagDto

internal fun FeatureFlag.toDto(): FeatureFlagDto =
    FeatureFlagDto(
        id = id,
        key = key,
        value = value,
    )