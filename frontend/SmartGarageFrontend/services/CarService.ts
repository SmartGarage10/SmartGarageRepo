export class CarService {
    static async fetchBrands(): Promise<string[]> {
        try {
            const res = await fetch("https://vpic.nhtsa.dot.gov/api/vehicles/GetMakesForVehicleType/car?format=json");
            if (!res.ok) {
                throw new Error("Failed to fetch brands");
            }

            const data = await res.json();
            return data.Results.map((entry: any) => entry.MakeName);
        } catch (error) {
            console.error("CarService.fetchBrands error:", error);
            return [];
        }
    }

    static async fetchModels(brand: string): Promise<string[]> {
        try {
            const url = `https://vpic.nhtsa.dot.gov/api/vehicles/getmodelsformake/${encodeURIComponent(brand)}?format=json`;
            console.log('Fetching models from:', url);

            const res = await fetch(url);
            console.log('Response status:', res.status);

            if (!res.ok) {
                throw new Error(`HTTP error! status: ${res.status}`);
            }

            const data = await res.json();
            console.log('Full API response:', data);

            if (!data.Results) {
                console.error('No Results field in response');
                return [];
            }

            // Filter models to ensure they belong to the requested brand
            // (some APIs might return models from similar brands)
            const filteredModels = data.Results
                .filter((entry: any) =>
                    entry.Make_Name.toLowerCase() === brand.toLowerCase()
                )
                .map((entry: any) => entry.Model_Name);

            console.log('Filtered models:', filteredModels);

            // Remove duplicates and sort alphabetically
            const uniqueModels = [...new Set(filteredModels)].sort((a, b) =>
                a.localeCompare(b)
            );

            return uniqueModels;
        } catch (error) {
            console.error(`CarService.fetchModels error for brand ${brand}:`, error);
            return [];
        }
    }
}