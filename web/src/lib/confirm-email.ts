import {LoaderFunction} from "react-router-dom";

export  const confirmEmailLoader: LoaderFunction<{token: string}> = async ({params} ) =>{
    return {success: true};
    return await fetch(`/api/confirm-email/${params.token}`)
        .then((response) => {
            if (response.status === 200) {
                return response.json();
            } else {
                return {success: false};
            }
        })
        .then((data) => {
            return {success: data.success};
        }).catch(
            () => {
                return {success: false};
            }
        );
}