package com.domain.usecase


import com.example.app.domain.model.*
import com.example.app.domain.rules.ContextRulesEngine

class InferContextUseCase(
    private val rulesEngine: ContextRulesEngine = ContextRulesEngine()
) {
    operator fun invoke(weather: WeatherInfo): Pair<ContextState, List<Recommendation>> {
        return rulesEngine.evaluate(weather)
    }
}