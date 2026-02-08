import { ChartAreaInteractive } from "@/components/dashboard-components/chart-area-interactive";
import { SectionCards } from "@/components/dashboard-components/section-cards";

// import data from "./data.json";

export default function Page() {
  return (
    <>
      <SectionCards />
      <div className="px-4 lg:px-6">
        <ChartAreaInteractive />
      </div>
    </>
  );
}
