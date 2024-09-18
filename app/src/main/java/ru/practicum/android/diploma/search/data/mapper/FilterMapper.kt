package ru.practicum.android.diploma.search.data.mapper

import ru.practicum.android.diploma.filter.domain.model.Area
import ru.practicum.android.diploma.filter.domain.model.Country
import ru.practicum.android.diploma.filter.domain.model.Industry
import ru.practicum.android.diploma.filter.domain.model.Location
import ru.practicum.android.diploma.search.data.dto.industries.IndustriesDto
import ru.practicum.android.diploma.search.data.dto.regions.AreasResponse
import ru.practicum.android.diploma.search.data.dto.regions.AreasResponseCountry

class FilterMapper {

    fun mapIndustriesResponseDtoToIndustries(industriesResponseDto: List<IndustriesDto>): List<Industry> {
        val industries = mutableListOf<Industry>()
        industriesResponseDto.forEach { industriesDto ->
            industries.add(
                Industry(
                    industriesDto.id,
                    industriesDto.name
                )
            )
            industriesDto.industries.forEach { industryDto ->
                industries.add(
                    Industry(
                        industryDto.id,
                        industryDto.name
                    )
                )
            }
        }
        return industries
    }

    fun mapAreasResponseCountryToCountries(areaResponse: AreasResponse): List<Country> {
        return areaResponse.map {
            Country(
                it.id,
                it.name
            )
        }
    }

    fun mapToLocations(areasResponse: AreasResponse): List<Location> {
        val locations = mutableListOf<Location>()

        areasResponse.forEach { areaResponseCountry ->
            val country = mapToCountry(areaResponseCountry)
            val areas = mapToAreas(areaResponseCountry)

            areas.forEach { area ->
                locations.add(Location(country, area))
            }
        }

        return locations
    }

    private fun mapToCountry(areaResponseCountry: AreasResponseCountry): Country {
        return Country(
            id = areaResponseCountry.id,
            name = areaResponseCountry.name
        )
    }

    private fun mapToAreas(areaResponseCountry: AreasResponseCountry): List<Area> {
        val areas = mutableListOf<Area>()

        areaResponseCountry.areas.forEach { areaRegion ->
            areas.add(
                Area(
                    id = areaRegion.id,
                    name = areaRegion.name,
                    parentId = areaRegion.parentId
                )
            )

            areaRegion.areas.forEach { areaRegionChild ->
                areas.add(
                    Area(
                        id = areaRegionChild.id,
                        name = areaRegionChild.name,
                        parentId = areaRegionChild.parentId
                    )
                )
            }
        }
        return areas
    }

}
