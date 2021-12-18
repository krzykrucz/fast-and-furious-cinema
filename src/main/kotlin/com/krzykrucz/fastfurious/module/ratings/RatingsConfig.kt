package com.krzykrucz.fastfurious.module.ratings

import com.krzykrucz.fastfurious.module.ratings.infrastructure.UUIDProvider
import java.time.Clock
import java.util.UUID

data class RatingsConfig(
    val clock: Clock = Clock.systemUTC(),
    val uuidProvider: UUIDProvider = { UUID.randomUUID() }
)