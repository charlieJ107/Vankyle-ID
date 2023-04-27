export const usernamePattern = /^[a-zA-Z][a-zA-Z0-9_.-]{3,20}$/;
export const emailPattern = /^\w+([-+.]\w+)*@\w+([-.]\w+)*\.\w+([-.]\w+)*$/;
export const passwordPattern = new RegExp(
    "(?=.*[a-z])" + // At least 1 lowercase letter
    // "(?=.*[A-Z])" + // At least 1 uppercase letter
    "(?=.*\\d)" +  // At least 1 number
    // "(?=.*[$@$!%*#?&])"+ // At least 1 common special symbols
    // "(?=.*[\u003A-\u0040])(?=.*[\u005B-\u0060])(?=.*[\u007B-\u007E])"+ // At least any ASCII special symbols
    "(?!.*[\u0000-\u0020])(?!.*[\u007E-\uFFFF])" + // No non-ascii characters
    "[^]" + // Any other things
    "{8,16}$" // 8-16 characters length
);

const cnPhonePattern: RegExp = /^\+861[0-9]{10}$/
const gbPhonePattern: RegExp = /^\+447[0-9]{9}$/
const phonePatternMap = new Map<string, RegExp>();
phonePatternMap.set("cn", cnPhonePattern);
phonePatternMap.set("gb", gbPhonePattern);

export function phonePatternTest(phone: string): boolean {
    let res = false;
    phonePatternMap.forEach((value) => {
        if (value.test(phone)) {
            res = true;
        }
    });
    return res;
}