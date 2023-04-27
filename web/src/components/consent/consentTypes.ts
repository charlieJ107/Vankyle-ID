export interface Scope {
    scope_id: string;
    scope_name: string;
    scope_description: string;
    approved: boolean;
}

export interface Client {
    client_id: string;
    client_name: string;
    client_description: string;
    logo_uri: string;
}