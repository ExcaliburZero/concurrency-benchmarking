import matplotlib.pyplot as plt
import os.path
import pandas as pd
import seaborn as sns

benchmark = "Benchmark"
mode = "Mode"
score = "Score"
error = "Score Error (99.9%)"
unit = "Unit"
server = "Server"
data_structure = "Data Structure"
test = "Test"
function = "Function"

scaled_score = "Scaled Score"
scaling_factor = "Scaling Factor"
scaled_unit = "Scaled Unit"
scaled_error = "Scaled Error"

custom = "custom"
standard = "standard"

plots_dir = os.path.join("webpage", "plots")

def main():
    data_files = [
        ("Wolf", "results_wolf_2.csv"),
        ("Rho", "results_rho_2.csv")
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
        data[function] = data[benchmark].map(getFunction)

        data[test] = data[server] + " " + data[data_structure]

        data[scaled_unit] = data[unit].map(lambda x: unit_names[x])
        data[scaling_factor] = data[unit].map(lambda x: unit_conversions[x])
        data[scaled_score] = data[score] * data[scaling_factor]
        data[scaled_error] = data[error] * data[scaling_factor]

        yield data

def getFunction(s):
    s2 = ""
    if custom in s:
        start = s.index(custom) + len(custom)
        s2 = s[start:]
    elif standard in s:
        start = s.index(standard) + len(standard)
        s2 = s[start:]

    return s2

mode_names = {
    "ss": "Single Shot",
    "thrpt": "Throughput",
    "avgt": "Average Time"
}

unit_names = {
    "s/op": "s/op",
    "ops/s": "ops/s",
    "us/op": "s/op",
    "ops/us": "ops/s"
}

unit_conversions = {
    "s/op": 1.0,
    "ops/s": 1.0,
    "us/op": 0.000001,
    "ops/us": 1000000
}

def plotData(data):
    modes = data[mode].unique()
    functions = data[function].unique()

    for m in modes:
        for f in functions:
            data_m = data[data[mode] == m]
            data_f = data_m[data_m[function] == f]
            value_units = data_f[scaled_unit].iloc[0]

            p = sns.barplot(x=test, y=scaled_score, data=data_f)
            p.get_axes().set_yscale('log')
            plt.title(f + " (" + mode_names[m] + ")")
            plt.ylabel("Mean " + value_units)

            plot_file_name = os.path.join(plots_dir, f + "_" + m + ".png")
            plt.savefig(plot_file_name)
            plt.clf()

if __name__ == "__main__":
    main()
