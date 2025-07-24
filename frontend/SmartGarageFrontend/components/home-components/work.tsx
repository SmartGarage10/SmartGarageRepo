import React from "react";
import { Card, CardContent, CardDescription, CardTitle } from "@/components/ui/card";
import { PenBoxIcon, RocketIcon, Search, SettingsIcon } from "lucide-react";

// Store icon components, not JSX elements, in workData
const workData = [
  {
    title: "Schedule Your Appointment",
    description:
      "Book your service easily online or by phone at a time that works best for you.",
    icon: PenBoxIcon,
  },
  {
    title: "Expert Diagnostics & Repair",
    description:
      "Our certified technicians perform thorough inspections and precise repairs using advanced tools.",
    icon: SettingsIcon,
  },
  {
    title: "Quality Assurance & Testing",
    description:
      "We test your vehicle to ensure every repair meets our high standards for safety and performance.",
    icon: Search,
  },
  {
    title: "Vehicle Delivery & Support",
    description:
      "We return your vehicle in optimal condition and remain available for any follow-up questions or service.",
    icon: RocketIcon,
  },
];

const iconSize = 40; // Change this value to set all icon sizes

export default function HowWeWork() {
  return (
    <section className="py-4 max-w-4xl mx-auto px-6">
      <div className="text-center mb-12">
        <h2 className="text-4xl font-extrabold mb-4 text-gray-400">
          How We Work
        </h2>
        <p className="text-gray-300 max-w-xl mx-auto leading-relaxed">
          Delivering expert auto care with a clear, step-by-step process to keep
          your vehicle in peak condition
        </p>
      </div>

      <div className="space-y-4">
        {workData.map(({ title, description, icon: Icon }, index) => (
          <div key={index} className="relative flex items-center p-4">
            {/* Step number */}
            <span
              className="absolute -left-8 top-1/2 -translate-y-1/2 font-extrabold text-[4rem] opacity-60 select-none pointer-events-none -z-10 text-indigo-500/60"
              style={{ fontFamily: "'YourHeadingFont', sans-serif" }}
            >
              {index + 1 < 10 ? `0${index + 1}` : index + 1}
            </span>

            {/* Card content */}
            <Card className="w-full rounded-2xl border-indigo-500/60">
              <CardContent className="flex items-center gap-8">
                {/* Icon */}
                <div className="flex-shrink-0 p-4 bg-indigo-500 rounded-xl grid place-items-center text-white">
                  <Icon size={iconSize} />
                </div>
                {/* Text content */}
                <div>
                  <CardTitle className="text-2xl font-semibold text-gray-300">
                    {title}
                  </CardTitle>
                  <CardDescription className="text-base text-gray-400 leading-relaxed">
                    {description}
                  </CardDescription>
                </div>
              </CardContent>
            </Card>
          </div>
        ))}
      </div>
    </section>
  );
}
