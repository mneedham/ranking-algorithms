def ranking_after_win(opts)
	ranking = opts[:ranking]
	opponent_ranking = opts[:opponent_ranking]
	importance = opts[:importance]

	expected = 1.0 / (1 + 10 ** ((ranking - opponent_ranking) / 400))
	ranking + importance * (1 - expected)
end