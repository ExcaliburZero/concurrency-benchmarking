import matplotlib.pyplot as plt
import os.path
import pandas as pd
import seaborn as sns

benchmark = "Benchmark"
mode = "Mode"
score = "Score"
unit = "Unit"
server = "Server"
data_structure = "Data Structure"
load = "Load"
test = "Test"

custom = "custom"
standard = "standard"

plots_dir = os.path.join("webpage", "plots")

def main():
    data_files = [
        ("Wolf", "results_wolf.csv"),
        ("Rho", "results_rho.csv")
    ]

    dfs = processDataFrames(data_files)

    final_data = pd.concat(dfs)
    print(final_data)

    plotData(final_data)

def processDataFrames(data_files):
    for (s, df) in data_files:
        data = pd.read_csv(df)

        data[server] = s
        data[data_structure] = data[benchmark].map(
                lambda x: custom if custom in x else standard)
        data[load] = data[benchmark].map(
                lambda x: 10 if "10" in x else 5)

        data[test] = data[server] + " " + data[data_structure]

        yield data

mode_names = {
    "ss": "Single Shot",
    "thrpt": "Throughput",
    "avgt": "Average Time"        
}

def plotData(data):
    modes = data[mode].unique()

    for m in modes:
        data_m = data[data[mode] == m]
        value_units = data_m[unit].iloc[0]

        sns.pointplot(x=load, y=score, hue=test, data=data_m)
        plt.title(mode_names[m])
        plt.xlabel("Load (users)")
        plt.ylabel("Mean " + value_units)

        plot_file_name = os.path.join(plots_dir, m + ".png")
        plt.savefig(plot_file_name)
        plt.clf()

if __name__ == "__main__":
    main()
