interface DocumentFactoryProvider {
    fun provideDocumentFactory(
        name: String = Defaults.churchName,
        fein: String = Defaults.fein,
        year: Int,
        filename: String = "${Defaults.filenamePrefix}$year",
    ): DocumentFactory
}