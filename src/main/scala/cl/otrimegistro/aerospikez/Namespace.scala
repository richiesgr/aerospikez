package cl.otrimegistro.aerospikez

case class Namespace(name: String = "test",
                     queryConfig: QueryConfig = QueryConfig(),
                     writeConfig: WriteConfig = WriteConfig())
