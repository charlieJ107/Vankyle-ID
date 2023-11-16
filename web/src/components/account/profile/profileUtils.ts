import {userManager} from "../../../auth/userManager";

interface BasicUserInfo {
    sub: string | null;
    name: string | null;
    email: string | null;
    email_verified: boolean;
    phone_number: string | null;
    phone_number_verified: boolean;
    picture: string | null;
    website: string | null;
    profile: string | null;
    gender: string | null;
    locale: string | null;
    preferred_username: string | null;
    updated_at: number;
    zoneInfo: string | null;
    birthdate: string | null;
    roles: string[] | null;
    nickname: string | null;
}

export type UserInfo = BasicUserInfo & {
    [key: string]: any;
}

export async function getUserInfo(): Promise<UserInfo> {
    const user = await userManager.getUser();
    if (!user) throw new Error("Not logged in");
    const response = await fetch("/api/userinfo", {
        method: "GET",
        headers: {
            Authorization: `${user.token_type} ${user.access_token}`
        }
    });
    if (response.ok) {
        return await response.json() as UserInfo;
    } else {
        throw new Error("Failed to get user info");
    }
}