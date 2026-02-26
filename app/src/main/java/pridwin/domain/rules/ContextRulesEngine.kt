//Nia Junod & Alina Tarasevich
package pridwin.domain.rules


import pridwin.domain.model.ContextState
import pridwin.domain.model.Recommendation
import pridwin.domain.model.WeatherInfo


class ContextRulesEngine {

    fun evaluate(weather: WeatherInfo): Pair<ContextState, List<Recommendation>> {
        val isRaining = weather.condition.contains("rain", ignoreCase = true)
        val isCold = weather.temperatureC < 10
        val isNight = !weather.isDay

        val context = ContextState(
            isRaining = isRaining,
            isCold = isCold,
            isNight = isNight
        )

        val recommendations = mutableListOf<Recommendation>()

        if (isRaining) {
            recommendations.add(
                Recommendation(
                    "Bring an Umbrella",
                    "Rain is expected. Stay dry!"
                )
            )
        }

        if (isCold) {
            recommendations.add(
                Recommendation(
                    "Wear a Jacket",
                    "It’s cold outside."
                )
            )
        }

        if (isNight) {
            recommendations.add(
                Recommendation(
                    "Drive Carefully",
                    "Low visibility during night."
                )
            )
        }

        return context to recommendations
    }
}