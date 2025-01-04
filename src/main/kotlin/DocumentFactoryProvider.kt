interface DocumentFactoryProvider {
    fun provideDocumentFactory(
        year: Int,
        name: String = Defaults.churchName(year),
        fein: String = Defaults.fein,
        filename: String = "${Defaults.churchAbbrev(year)}${Defaults.filenamePrefix}$year",
    ): DocumentFactory
}