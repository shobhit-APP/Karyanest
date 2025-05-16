package com.backend.karyanestApplication.Helper;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

@Component
public class trackingActivity {
    public String Activity(HttpServletRequest request)
    {
        String uri = request.getRequestURI();
        String method = request.getMethod();
        String activityType = "GENERAL";
        if (uri.startsWith("/v1/users")) {
            if (uri.contains("/register")) {
                activityType = "USER_REGISTERED";
            } else if (uri.contains("/profile")) {
                activityType = switch (method) {
                    case "GET" -> "USER_PROFILE_VIEWED";
                    case "PUT" -> "USER_PROFILE_UPDATED";
                    default -> "USER_PROFILE_ACTION";
                };
            } else if (uri.contains("/password")) {
                activityType = "USER_PASSWORD_CHANGED";
            } else if (uri.contains("/verify-otp")) {
                activityType = "USER_VERIFIED";
            } else {
                activityType = switch (method) {
                    case "POST" -> "USER_CREATED";
                    case "GET" -> "USER_FETCHED";
                    case "PUT" -> "USER_UPDATED";
                    case "DELETE" -> "USER_DELETED";
                    default -> "USER_ACTION";
                };
            }
        }
        else if (uri.startsWith("/v1/props")) {
            if (uri.matches("^/v1/props/search.*")) {
                activityType = "PROPERTY_SEARCHED";
            } else if (uri.matches("^/v1/props/resources/\\d+$")) {
                activityType = switch (method) {
                    case "POST" -> "PROPERTY_RESOURCE_ADDED";
                    case "PUT" -> "PROPERTY_RESOURCE_UPDATED";
                    default -> "PROPERTY_RESOURCE_ACTION";
                };
            } else if (uri.matches("^/v1/props/user/\\d+$")) {
                activityType = "PROPERTIES_FETCHED_BY_USER";
            } else if (uri.matches("^/v1/props/currentUserProperty.*")) {
                activityType = "CURRENT_USER_PROPERTIES_FETCHED";
            } else if (uri.matches("^/v1/props/visits/\\d+$")) {
                activityType = "PROPERTY_VISITS_FETCHED";
            } else if (uri.matches("^/v1/props/visits_count/\\d+$")) {
                activityType = "PROPERTY_VISIT_COUNT_FETCHED";
            } else if (uri.matches("^/v1/props/record-visit/\\d+$")) {
                activityType = "PROPERTY_VISIT_RECORDED";
            } else if (uri.matches("^/v1/props/ChangePrice/\\d+$")) {
                activityType = "PROPERTY_PRICE_UPDATED";
            } else if (uri.matches("^/v1/props/price_history$")) {
                activityType = "PROPERTY_PRICE_HISTORY_FETCHED";
            } else if (uri.matches("^/v1/props/price_history/\\d+$")) {
                activityType = "PROPERTY_PRICE_HISTORY_BY_ID_FETCHED";
            } else if (uri.matches("^/v1/props/\\d+$")) {
                // If it matches /v1/props/{id}
                activityType = switch (method) {
                    case "GET" -> "PROPERTY_FETCHED_BY_ID";
                    case "PUT" -> "PROPERTY_UPDATED";
                    case "DELETE" -> "PROPERTY_DELETED";
                    default -> "PROPERTY_ID_OPERATION";
                };
            } else if (uri.equals("/v1/props")) {
                activityType = switch (method) {
                    case "GET" -> "ALL_PROPERTIES_FETCHED";
                    case "POST" -> "PROPERTY_CREATED";
                    default -> "PROPERTY_ACTION";
                };
            } else {
                activityType = "PROPERTY_ACTION";
            }
        }else if (uri.startsWith("/v1/addresses")) {
        if (uri.matches("^/v1/addresses/search.*")) {
            activityType = "ADDRESS_SEARCHED";
        } else if (uri.matches("^/v1/addresses/city.*")) {
            activityType = "ADDRESS_FILTERED_BY_CITY";
        } else if (uri.matches("^/v1/addresses/area.*")) {
            activityType = "ADDRESS_FILTERED_BY_AREA";
        } else if (uri.matches("^/v1/addresses/district.*")) {
            activityType = "ADDRESS_FILTERED_BY_DISTRICT";
        } else if (uri.matches("^/v1/addresses/state.*")) {
            activityType = "ADDRESS_FILTERED_BY_STATE";
        } else if (uri.matches("^/v1/addresses/pincode.*")) {
            activityType = "ADDRESS_FILTERED_BY_PINCODE";
        } else if (uri.matches("^/v1/addresses/\\d+$")) {
            // If it matches /v1/addresses/{id}
            activityType = switch (method) {
                case "GET" -> "ADDRESS_FETCHED_BY_ID";
                case "PUT" -> "ADDRESS_UPDATED";
                case "DELETE" -> "ADDRESS_DELETED";
                default -> "ADDRESS_ID_OPERATION";
            };
        } else if (uri.equals("/v1/addresses")) {
            activityType = switch (method) {
                case "GET" -> "ALL_ADDRESSES_FETCHED";
                case "POST" -> "ADDRESS_CREATED";
                default -> "ADDRESS_ACTION";
            };
        } else {
            activityType = "ADDRESS_ACTION";
        }
    }
        else if (uri.startsWith("/v1/admin")) {
            activityType = switch (method) {
                case "POST" -> {
                    if (uri.contains("create_roles")) yield "ROLE_CREATED";
                    else if (uri.contains("create_permits")) yield "PERMISSION_CREATED";
                    else if (uri.contains("create_routes")) yield "ROUTE_CREATED";
                    else if (uri.contains("assign_permission_to_role")) yield "PERMISSION_ASSIGNED_TO_ROLE";
                    else yield "ADMIN_POST_ACTION";
                }
                case "GET" -> {
                    if (uri.contains("get_roles")) yield "ROLES_FETCHED";
                    else if (uri.contains("get_permits")) yield "PERMISSIONS_FETCHED";
                    else yield "ADMIN_GET_ACTION";
                }
                case "PATCH" -> {
                    if (uri.contains("update_user_role")) yield "USER_ROLE_UPDATED";
                    else yield "ADMIN_PATCH_ACTION";
                }
                case "DELETE" -> "ADMIN_DELETE_ACTION";
                default -> "ADMIN_ACTION";
            };
        }
        else if (uri.startsWith("/v1/auth")) {
            activityType = switch (method) {
                case "POST" -> {
                    if (uri.contains("/register")) yield "USER_REGISTERED";
                    else if (uri.contains("/login")) yield "USER_LOGIN";
                    else if (uri.contains("/logout")) yield "USER_LOGOUT";
                    else if (uri.contains("/verify")) yield "USER_VERIFICATION";
                    else if (uri.contains("/forget-password")) yield "PASSWORD_RESET_INITIATED";
                    else if (uri.contains("/reset-password")) yield "PASSWORD_RESET_COMPLETED";
                    else yield "AUTH_ACTION";
                }
                case "GET" -> {
                    if (uri.contains("/validateReferenceToken")) yield "TOKEN_VALIDATED";
                    else yield "AUTH_VIEWED";
                }
                default -> "AUTH_ACTION";
            };
        }
        else if (uri.startsWith("/v1/chat")) {
            activityType = switch (method) {
                case "POST" -> {
                    if (uri.contains("/start")) yield "CHAT_STARTED";
                    else if (uri.contains("/send")) yield "MESSAGE_SENT";
                    else if (uri.contains("/close")) yield "CHAT_CLOSED";
                    else yield "CHAT_ACTION";
                }
                case "GET" -> {
                    if (uri.contains("/messages")) yield "MESSAGES_VIEWED";
                    else yield "CHAT_VIEWED";
                }
                case "PUT" -> "CHAT_UPDATED";
                case "DELETE" -> "CHAT_DELETED";
                default -> "CHAT_ACTION";
            };
        }
        else if (uri.startsWith("/v1/faqs")) {
            activityType = switch (method) {
                case "POST" -> "FAQ_CREATED";
                case "GET" -> "FAQ_VIEWED";
                case "PATCH" -> "FAQ_UPDATED";
                case "DELETE" -> "FAQ_DELETED";
                default -> "FAQ_ACTION";
            };
        }
        else if (uri.startsWith("/v1/home")) {
            activityType = switch (method) {
                case "GET" -> {
                    if (uri.contains("/welcome")) yield "HOME_WELCOME_VIEWED";
                    else yield "HOME_PAGE_VIEWED";
                }
                case "POST" -> "HOME_ACTION_CREATED";
                case "PUT" -> "HOME_ACTION_UPDATED";
                case "DELETE" -> "HOME_ACTION_DELETED";
                default -> "HOME_ACTION";
            };
        }
        else if (uri.startsWith("/v1/inquiries")) {
            activityType = switch (method) {
                case "POST" -> "INQUIRY_CREATED";
                case "GET" -> {
                    if (uri.contains("/user/")) yield "USER_INQUIRIES_VIEWED";
                    else if (uri.contains("/property/")) yield "PROPERTY_INQUIRIES_VIEWED";
                    else yield "ALL_INQUIRIES_VIEWED";
                }
                case "PUT" -> "INQUIRY_UPDATED";
                case "DELETE" -> "INQUIRY_DELETED";
                default -> "INQUIRY_ACTION";
            };
        }
        else if (uri.startsWith("/v1/lead-notes")) {
            activityType = switch (method) {
                case "POST" -> "LEAD_NOTE_CREATED";
                case "GET" -> {
                    if (uri.contains("/lead/")) yield "LEAD_NOTES_VIEWED_BY_LEAD";
                    else yield "LEAD_NOTE_VIEWED";
                }
                case "PUT" -> "LEAD_NOTE_UPDATED";
                case "DELETE" -> "LEAD_NOTE_DELETED";
                default -> "LEAD_NOTE_ACTION";
            };
        }
       else  if (uri.startsWith("/v1/leads")) {
        activityType = switch (method) {
            case "POST" -> "LEAD_CREATED";
            case "GET" -> "LEAD_VIEWED";
            case "PUT" -> "LEAD_UPDATED";
            case "DELETE" -> "LEAD_DELETED";
            default -> "LEAD_ACTION";
        };
    } else if (uri.startsWith("/v1/favorites")) {
            activityType = switch (method) {
                case "POST" -> "FAVORITE_ADDED";
                case "GET" -> "FAVORITES_VIEWED";
                case "DELETE" -> "FAVORITE_REMOVED";
                default -> "FAVORITE_ACTION";
            };
        } else if (uri.startsWith("/v1/terms")) {
            activityType = switch (method) {
                case "GET" -> "TERMS_VIEWED";
                case "POST" -> "TERMS_UPDATE";
                default -> "TERMS_ACTION";
            };
        }
         else if (uri.startsWith("/v1/user_activity")) {
            activityType = switch (method) {
                case "GET" -> "LOGS_VIEWED";
                case "POST" -> "UNKNOWN";
                default -> "USER_ACTIVITY_ACTION";
            };
        }
        else {
            activityType = "UNKNOWN_ACTIVITY";
        }
        return activityType;
    }
}
