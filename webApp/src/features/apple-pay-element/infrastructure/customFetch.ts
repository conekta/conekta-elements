export const customFetch = async <T>(url: string, config?: RequestInit): Promise<T> => {
    try {
        const response = await fetch(url, config);
        return response.json();
    } catch (e: any) {
        throw e;
    }
};
