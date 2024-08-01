package com.example.fibertel

object ApiEndpoints {
    const val CLIENTS_BY_DNI = "clients?national_identification_number_eq="
    const val INVOICING_BY_DNI = "invoicing/invoices?client_national_identification_number_eq="
    const val TICKETS = "help_desk/issues"
    const val TICKET_DETAILS = "help_desk/issues/"
    const val BASE_URL = "https://www.cloud.wispro.co/api/v1"
}
