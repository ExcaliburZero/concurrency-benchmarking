import matplotlib.pyplot as plt
import os.path
import pandas as pd

def main():
    benchmark = "Benchmark"
    mode = "Mode"
    score = "Score"
    unit = "Unit"

    custom = "custom"
    standard = "standard"

    data_file = "results.csv"
    plots_dir = "plots"

    data = pd.read_csv(data_file)

    print(data)

    benchmarks = data[benchmark].unique()

    custom_benchmarks = [b for b in benchmarks if custom in b]
    standard_benchmarks = [b.replace(custom, standard) for b in custom_benchmarks]

    for (c, s) in zip(custom_benchmarks, standard_benchmarks):
        c_score = data[data[benchmark] == c][score].iloc[0]
        s_score = data[data[benchmark] == s][score].iloc[0]

        score_unit = data[data[benchmark] == s][unit].iloc[0]
        score_name = data[data[benchmark] == s][benchmark].iloc[0].replace(standard, "")
        score_mode = data[data[benchmark] == s][mode].iloc[0]

        objects = [custom, standard]
        pos = [0, 1]
        performance = [c_score, s_score]

        plt.bar(pos, performance, align="center", alpha=0.5)
        plt.xticks(pos, objects)
        plt.ylabel(score_mode + " (" + score_unit + ")")
        plt.title(score_name)

        plot_file_name = os.path.join(plots_dir, score_name.replace(".", "-") + ".png")
        plt.savefig(plot_file_name)
        plt.clf()

if __name__ == "__main__":
    main()
