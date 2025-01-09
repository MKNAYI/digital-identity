package com.proof_backend.utils;

public class Constants {
    public static final String ALGORITHM = "AES";
    public static final String TRANSFORMATION = "AES";
    public static String STRIPE_PRODUCT_NAME_BUSINESS_TYPE = "Business Type";
    public static String STRIPE_PRODUCT_NAME_INDIVIDUAL_TYPE = "Individual Type";

    public static String STRIPE_WEB_HOOK_EVENT_TYPE_ATTRIBUTE_ID ="id";
    public static String STRIPE_WEB_HOOK_EVENT_TYPE_ATTRIBUTE_TYPE ="type";
    public static String STRIPE_WEB_HOOK_EVENT_TYPE_CUSTOMER_SUBSCRIPTION_TRIAL_WILL_END ="customer.subscription.trial_will_end";
    public static String STRIPE_WEB_HOOK_EVENT_TYPE_CUSTOMER_SUBSCRIPTION_PENDING_UPDATE_EXPIRED ="customer.subscription.pending_update_expired";
    public static String STRIPE_WEB_HOOK_EVENT_TYPE_CUSTOMER_SUBSCRIPTION_UPDATED ="customer.subscription.updated";
    public static String STRIPE_WEB_HOOK_EVENT_TYPE_CUSTOMER_SUBSCRIPTION_RESUMED ="customer.subscription.resumed";
    public static String STRIPE_WEB_HOOK_EVENT_TYPE_CUSTOMER_SUBSCRIPTION_PENDING_UPDATE_APPLIED ="customer.subscription.pending_update_applied";
    public static String STRIPE_WEB_HOOK_EVENT_TYPE_CUSTOMER_SUBSCRIPTION_CREATED ="customer.subscription.created";
    public static String STRIPE_WEB_HOOK_EVENT_TYPE_CUSTOMER_SUBSCRIPTION_DELETED ="customer.subscription.deleted";

    public static String STRIPE_WEB_HOOK_EVENT_DATA_ATTRIBUTE_NAME ="data";
    public static String STRIPE_WEB_HOOK_EVENT_TYPE_DATA_OBJECT_ATTRIBUTE_NAME ="object";
    public static String SUBSCRIPTION_ID ="id";
    public static String SUBSCRIPTION_LATEST_INVOICE_ATTRIBUTE_NAME ="latest_invoice";

    public static String SUBSCRIPTION_STATUS ="status";
    public static String SUBSCRIPTION_STATUS_ACTIVE ="active";
    public static String SUBSCRIPTION_STATUS_TRIALING ="trialing";
    public static String SUBSCRIPTION_STATUS_INCOMPLETE_EXPIRED ="incomplete_expired";
    public static String SUBSCRIPTION_STATUS_UNPAID ="unpaid";
    public static String SUBSCRIPTION_STATUS_PAST_DUE ="past_due";
    public static String SUBSCRIPTION_STATUS_CANCELED ="canceled";

    public static String SUBSCRIPTION_CURRENT_PERIOD_START ="current_period_start";
    public static String SUBSCRIPTION_CURRENT_PERIOD_END ="current_period_end";

    public static String INVOICE_STATUS_PAID ="paid";
    public static String INVOICE_STATUS_DRAFT ="draft";
    public static String INVOICE_STATUS_VOID ="void";
    public static String INVOICE_STATUS_OPEN ="open";
    public static String INVOICE_STATUS_UNCOLLECTIBLE ="uncollectible";

    public static String ID_ANALYZER_DOCUMENT_ATTRIBUTE_SUCCESS ="success";
    public static String ID_ANALYZER_DOCUMENT_ATTRIBUTE_DECISION ="decision";
    public static String ID_ANALYZER_DOCUMENT_ATTRIBUTE_DECISION_ACCEPT ="accept";
    public static String ID_ANALYZER_DOCUMENT_ATTRIBUTE_DECISION_REJECT ="reject";

    public static String ID_ANALYZER_DOCUMENT_ATTRIBUTE_PROFILE_ID ="profileId";
    public static String ID_ANALYZER_DOCUMENT_ATTRIBUTE_DATA ="data";

    public static String ID_ANALYZER_DOCUMENT_ATTRIBUTE_DATA_DOCUMENT_TYPE ="documentType";
    public static String ID_ANALYZER_DOCUMENT_ATTRIBUTE_DATA_DOCUMENT_TYPE_D ="D";
    public static String ID_ANALYZER_DOCUMENT_ATTRIBUTE_DATA_DOCUMENT_TYPE_P ="P";
    public static String ID_ANALYZER_DOCUMENT_ATTRIBUTE_DATA_DOCUMENT_TYPE_I ="I";

    public static String ID_ANALYZER_DOCUMENT_ATTRIBUTE_DATA_FULLNAME ="fullName";
    public static String ID_ANALYZER_DOCUMENT_ATTRIBUTE_DATA_SEX ="sex";
    public static String ID_ANALYZER_DOCUMENT_ATTRIBUTE_DATA_SEX_M ="M";
    public static String ID_ANALYZER_DOCUMENT_ATTRIBUTE_DATA_SEX_F ="F";


    public static String ID_ANALYZER_DOCUMENT_ATTRIBUTE_DATA_DOB ="dob";
    public static String ID_ANALYZER_DOCUMENT_ATTRIBUTE_DATA_NATIONALITY_FULL ="nationalityFull";
    public static String ID_ANALYZER_DOCUMENT_ATTRIBUTE_DATA_ISSUED ="issued";
    public static String ID_ANALYZER_DOCUMENT_ATTRIBUTE_DATA_EXPIRY ="expiry";
    public static String ID_ANALYZER_DOCUMENT_ATTRIBUTE_DATA_STATE_FULL ="stateFull";
    public static String ID_ANALYZER_DOCUMENT_ATTRIBUTE_DATA_COUNTRY_FULL ="countryFull";

    public static String ID_ANALYZER_DOCUMENT_ATTRIBUTE_DATA_WARNING ="warning";
    public static String ID_ANALYZER_DOCUMENT_ATTRIBUTE_DATA_WARNING_DESCRIPTION ="description";

    public static String ID_ANALYZER_DOCUMENT_ATTRIBUTE_VALUE ="value";
}
