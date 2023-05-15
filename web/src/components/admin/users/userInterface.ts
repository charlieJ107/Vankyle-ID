export interface UserInterface {
    id: string;
    username: string;
    password: string | null;
    authorities: string[];
    roles: string[];
    accountExpired: boolean;
    accountLocked: boolean;
    credentialsExpired: boolean;
    enabled: boolean;
    mfaEnabled: boolean;
    name: string;
    email: string;
    emailVerified: boolean;
    phone: string;
    phoneVerified: boolean;
    picture: string;
}